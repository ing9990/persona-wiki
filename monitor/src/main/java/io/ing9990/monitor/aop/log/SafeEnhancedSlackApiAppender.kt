package io.ing9990.monitor.aop.log

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.UnsynchronizedAppenderBase
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 안전한 슬랙 로그 전송을 위한 Appender
 * 모든 예외 상황에서 graceful하게 실패하며 메인 애플리케이션에 영향을 주지 않습니다.
 */
class SafeEnhancedSlackApiAppender : UnsynchronizedAppenderBase<ILoggingEvent>() {
    // Slack API 관련 설정
    var webhookUrl: String = ""
    var webhookUri: String = "" // 이전 버전과의 호환성을 위해
    var channel: String = "#api-monitoring"
    var username: String = "국민사형투표"
    var iconEmoji: String = ":bar_chart:"
    var enableSlackLogging: Boolean = true // 슬랙 로깅 활성화 여부
    var connectTimeout: Int = 3000 // 연결 타임아웃(ms)
    var readTimeout: Int = 3000 // 읽기 타임아웃(ms)
    var asyncThreads: Int = 1 // 비동기 처리 스레드 수
    var maxRetries: Int = 2 // 최대 재시도 횟수

    // 로그 필터링 관련 설정
    private val apiMonitor = MarkerFactory.getMarker("API_MONITORING")
    private val market500 = MarkerFactory.getMarker("ERROR_500")

    // 통계 정보
    private val successCount = AtomicInteger(0)
    private val failureCount = AtomicInteger(0)
    private val lastFailureTime = AtomicInteger(0)

    // HTTP 클라이언트
    private val restTemplate = RestTemplate()
    private val objectMapper = ObjectMapper()

    // 비동기 처리를 위한 ExecutorService
    private lateinit var executorService: ExecutorService

    override fun start() {
        if (getEffectiveWebhookUrl().isBlank()) {
            addWarn("webhookUrl이 설정되지 않았습니다. 슬랙 로깅이 비활성화됩니다.")
            enableSlackLogging = false
        }

        try {
            // 비동기 ExecutorService 생성
            executorService =
                Executors.newFixedThreadPool(asyncThreads) { r ->
                    val thread = Thread(r, "slack-logger-thread")
                    thread.isDaemon = true // 메인 애플리케이션 종료를 방해하지 않음
                    thread.uncaughtExceptionHandler =
                        Thread.UncaughtExceptionHandler { t, e ->
                            addError("Slack 로깅 스레드에서 처리되지 않은 예외 발생: ${t.name}", e)
                        }
                    thread
                }

            addInfo(
                "io.ing9990.monitor.aop.log.SafeEnhancedSlackApiAppender 시작: webhookUrl=${
                    maskUrl(
                        getEffectiveWebhookUrl(),
                    )
                }, channel=$channel",
            )
            super.start()
        } catch (e: Exception) {
            addError("io.ing9990.monitor.aop.log.SafeEnhancedSlackApiAppender 시작 중 오류 발생", e)
            enableSlackLogging = false // 안전하게 비활성화
        }
    }

    override fun stop() {
        try {
            executorService.shutdown()
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow()
            }
            addInfo("Slack 로깅 통계: 성공=${successCount.get()}, 실패=${failureCount.get()}")
        } catch (e: Exception) {
            addError("ExecutorService 종료 중 오류 발생", e)
        } finally {
            super.stop()
        }
    }

    override fun append(event: ILoggingEvent) {
        if (!enableSlackLogging || !isStarted) {
            return
        }

        try {
            // 마커 또는 레벨 기반 필터링
            if (!shouldSendToSlack(event)) {
                return
            }

            // 비동기로 Slack에 전송 (CompletableFuture 사용)
            CompletableFuture
                .runAsync({
                    try {
                        val message = formatMessage(event)
                        sendToSlack(message, event)
                    } catch (e: Exception) {
                        addError("Slack 메시지 형식화 중 오류 발생", e)
                        failureCount.incrementAndGet()
                    }
                }, executorService)
                .exceptionally { e ->
                    addError("Slack 비동기 처리 중 오류 발생", e)
                    null
                }
        } catch (e: Exception) {
            // 모든 예외를 잡아서 메인 애플리케이션에 영향을 주지 않음
            addError("Slack 로깅 처리 중 예기치 않은 오류 발생", e)
            failureCount.incrementAndGet()
        }
    }

    private fun getEffectiveWebhookUrl(): String =
        when {
            webhookUrl.isNotBlank() -> webhookUrl
            webhookUri.isNotBlank() -> webhookUri
            else -> ""
        }

    private fun maskUrl(url: String): String =
        if (url.length > 15) {
            "${url.substring(0, 15)}...${url.takeLast(5)}"
        } else {
            url
        }

    private fun shouldSendToSlack(event: ILoggingEvent): Boolean {
        // API_MONITORING 마커가 있거나 ERROR 레벨인 경우에만 Slack으로 전송
        return try {
            containsMarker(event, apiMonitor) ||
                containsMarker(event, market500) ||
                event.level == Level.ERROR
        } catch (e: Exception) {
            addError("마커 확인 중 오류 발생", e)
            false // 안전하게 전송 안함
        }
    }

    private fun containsMarker(
        event: ILoggingEvent,
        marker: Marker,
    ): Boolean {
        val eventMarker = event.marker ?: return false
        return eventMarker.contains(marker)
    }

    private fun formatMessage(event: ILoggingEvent): SlackMessage {
        val messageText = event.formattedMessage
        val timestamp =
            LocalDateTime
                .ofInstant(
                    Instant.ofEpochMilli(event.timeStamp),
                    ZoneId.systemDefault(),
                ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        // 로그 메시지에서 정보 추출
        val messageInfo = parseLogMessage(messageText)

        // 로그 레벨에 따른 색상 설정
        val color =
            when (event.level) {
                Level.ERROR -> "#FF5252" // 빨간색
                Level.WARN -> "#FFC107" // 노란색
                Level.INFO ->
                    when {
                        messageInfo["실행시간"]
                            ?.replace("ms", "")
                            ?.trim()
                            ?.toIntOrNull() ?: 0 > 1000 -> "#FF9800" // 주황색 (느린 API)
                        else -> "#4CAF50" // 녹색 (정상 API)
                    }

                else -> "#2196F3" // 파란색 (기타)
            }

        // 슬랙 메시지 구성
        return createSlackMessage(event, messageInfo, color, timestamp)
    }

    private fun parseLogMessage(message: String): Map<String, String> {
        val result = mutableMapOf<String, String>()

        try {
            // 간단한 파싱 로직 (키-값 추출)
            val lines = message.split("\n")

            if (lines.isNotEmpty()) {
                result["title"] = lines[0].trim()

                for (i in 1 until lines.size) {
                    val line = lines[i].trim()
                    val colonIndex = line.indexOf(":")

                    if (colonIndex > 0) {
                        val key = line.substring(0, colonIndex).trim()
                        val value = line.substring(colonIndex + 1).trim()
                        result[key] = value
                    }
                }
            }
        } catch (e: Exception) {
            addError("로그 메시지 파싱 중 오류 발생", e)
            // 파싱 실패해도 빈 맵 반환
        }

        return result
    }

    private fun createSlackMessage(
        event: ILoggingEvent,
        messageInfo: Map<String, String>,
        color: String,
        timestamp: String,
    ): SlackMessage {
        // 기본 메시지 정보
        val path = messageInfo["경로"] ?: "N/A"
        val executionTime = messageInfo["실행시간"]?.replace("ms", "")?.trim()?.toIntOrNull() ?: 0
        val statusCode = messageInfo["상태코드"]?.toIntOrNull() ?: 200
        val clientIp = messageInfo["IP"] ?: "N/A"
        val userAgent = messageInfo["User-Agent"] ?: "N/A"
        val errorMessage = messageInfo["에러메시지"] ?: messageInfo["message"] ?: ""

        // Slack 메시지 구성
        val blocks = mutableListOf<Map<String, Any>>()

        try {
            // 헤더 섹션
            blocks.add(
                mapOf(
                    "type" to "header",
                    "text" to
                        mapOf(
                            "type" to "plain_text",
                            "text" to
                                when {
                                    event.level == Level.ERROR -> "🚨 API 오류 발생"
                                    executionTime > 1000 -> "⚠️ 느린 API 응답"
                                    else -> "✅ API 모니터링"
                                },
                            "emoji" to true,
                        ),
                ),
            )

            // 메인 섹션
            blocks.add(
                mapOf(
                    "type" to "section",
                    "fields" to
                        listOf(
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to "*요청 경로:*\n`$path`",
                            ),
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to "*상태:*\n${getStatusEmoji(statusCode)} `$statusCode`",
                            ),
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to "*실행 시간:*\n${getPerformanceEmoji(executionTime)} `${executionTime}ms`",
                            ),
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to "*IP 주소:*\n`$clientIp`",
                            ),
                        ),
                ),
            )

            // 에러 메시지가 있는 경우 추가
            if (errorMessage.isNotEmpty()) {
                blocks.add(
                    mapOf(
                        "type" to "section",
                        "text" to
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to "*오류 메시지:*\n```${errorMessage.take(1000)}${if (errorMessage.length > 1000) "..." else ""}```",
                            ),
                    ),
                )
            }

            // User-Agent 정보 추가
            blocks.add(
                mapOf(
                    "type" to "context",
                    "elements" to
                        listOf(
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to "*User-Agent:* $userAgent",
                            ),
                        ),
                ),
            )

            // 타임스탬프 추가
            blocks.add(
                mapOf(
                    "type" to "context",
                    "elements" to
                        listOf(
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to ":clock2: $timestamp",
                            ),
                        ),
                ),
            )

            // 디바이더 추가
            blocks.add(mapOf("type" to "divider"))
        } catch (e: Exception) {
            addError("Slack 메시지 블록 생성 중 오류 발생", e)
            // 오류 발생 시 최소한의 메시지만 포함
            blocks.clear()
            blocks.add(
                mapOf(
                    "type" to "section",
                    "text" to
                        mapOf(
                            "type" to "mrkdwn",
                            "text" to "*로깅 오류*\n메시지 형식화 중 오류가 발생했습니다. 원본 메시지: ${
                                event.formattedMessage.take(
                                    100,
                                )
                            }...",
                        ),
                ),
            )
        }

        return SlackMessage(
            username = username,
            channel = channel,
            attachments =
                listOf(
                    mapOf(
                        "color" to color,
                        "blocks" to blocks,
                    ),
                ),
            icon_emoji = iconEmoji,
        )
    }

    private fun getStatusEmoji(statusCode: Int): String =
        when {
            statusCode < 300 -> "🟢" // 성공
            statusCode < 400 -> "🔵" // 리다이렉션
            statusCode < 500 -> "🟠" // 클라이언트 오류
            else -> "🔴" // 서버 오류
        }

    private fun getPerformanceEmoji(executionTime: Int): String =
        when {
            executionTime < 100 -> "⚡" // 매우 빠름
            executionTime < 300 -> "🚀" // 빠름
            executionTime < 1000 -> "🏃" // 보통
            executionTime < 3000 -> "🐢" // 느림
            else -> "🐌" // 매우 느림
        }

    private fun sendToSlack(
        message: SlackMessage,
        event: ILoggingEvent,
        retryCount: Int = 0,
    ) {
        if (!enableSlackLogging) {
            return
        }

        try {
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON

            val requestBody = objectMapper.writeValueAsString(message)
            val request = HttpEntity(requestBody, headers)

            val response =
                restTemplate.postForEntity(getEffectiveWebhookUrl(), request, String::class.java)

            if (response.statusCode.is2xxSuccessful) {
                successCount.incrementAndGet()
            } else {
                addWarn("Slack API 응답 오류: ${response.statusCode} - ${response.body}")
                handleFailure(
                    message,
                    event,
                    retryCount,
                    RuntimeException("Slack API 응답 오류: ${response.statusCode}"),
                )
            }
        } catch (e: ResourceAccessException) {
            // 네트워크 문제 - 재시도 가능
            addWarn("Slack API 접근 오류 (네트워크 문제): ${e.message}")
            handleFailure(message, event, retryCount, e)
        } catch (e: Exception) {
            // 기타 오류
            addError("Slack API 호출 중 예외 발생", e)
            handleFailure(message, event, retryCount, e)
        }
    }

    private fun handleFailure(
        message: SlackMessage,
        event: ILoggingEvent,
        retryCount: Int,
        e: Exception,
    ) {
        failureCount.incrementAndGet()
        lastFailureTime.set((System.currentTimeMillis() / 1000).toInt())

        // 최대 재시도 횟수를 초과하지 않았으면 재시도
        if (retryCount < maxRetries) {
            val delayMs = (retryCount + 1) * 1000L // 지수 백오프 (1초, 2초, ...)
            try {
                Thread.sleep(delayMs)
                sendToSlack(message, event, retryCount + 1)
            } catch (ie: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    // Slack API 메시지 구조
    data class SlackMessage(
        val username: String,
        val channel: String,
        val attachments: List<Map<String, Any>>,
        val icon_emoji: String,
    )
}
