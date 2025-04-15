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
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class EnhancedSlackApiAppender : UnsynchronizedAppenderBase<ILoggingEvent>() {
    // Slack API 관련 설정
    var webhookUrl: String =
        "https://hooks.slack.com/services/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"
    var channel: String = "#api-monitoring"
    var username: String = "국민사형투표"
    var iconEmoji: String = ":bar_chart:"

    // 로그 필터링 관련 설정
    private val API_MONITORING_MARKER = MarkerFactory.getMarker("API_MONITORING")
    private val ERROR_500_MARKER = MarkerFactory.getMarker("ERROR_500")

    // HTTP 클라이언트
    private val restTemplate = RestTemplate()
    private val objectMapper = ObjectMapper()

    // 비동기 처리를 위한 ExecutorService
    private val executorService = Executors.newSingleThreadExecutor()

    override fun start() {
        if (webhookUrl.isBlank()) {
            addError("webhookUrl가 설정되지 않았습니다")
            return
        }

        addInfo("EnhancedSlackApiAppender 시작: webhookUrl=$webhookUrl, channel=$channel")
        super.start()
    }

    override fun stop() {
        executorService.shutdown()
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executorService.shutdownNow()
        }
        super.stop()
    }

    override fun append(event: ILoggingEvent) {
        // 마커 또는 레벨 기반 필터링
        if (!shouldSendToSlack(event)) {
            return
        }

        // 비동기로 Slack에 전송
        executorService.submit {
            try {
                val message = formatMessage(event)
                sendToSlack(message, event)
            } catch (e: Exception) {
                addError("Error posting log to Slack: ${e.message}", e)
            }
        }
    }

    private fun shouldSendToSlack(event: ILoggingEvent): Boolean = true

    private fun containsMarker(
        event: ILoggingEvent,
        marker: Marker,
    ): Boolean {
        println("⚠️ containsMarker: event.marker=${event.marker}, target=$marker")
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
                            "text" to "*오류 메시지:*\n```$errorMessage```",
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
    ) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestBody = objectMapper.writeValueAsString(message)
        val request = HttpEntity(requestBody, headers)

        try {
            val response = restTemplate.postForEntity(webhookUrl, request, String::class.java)
            if (!response.statusCode.is2xxSuccessful) {
                addError("Slack API 응답 오류: ${response.statusCode} - ${response.body}")
            }
        } catch (e: Exception) {
            addError("Slack API 호출 중 예외 발생", e)
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
