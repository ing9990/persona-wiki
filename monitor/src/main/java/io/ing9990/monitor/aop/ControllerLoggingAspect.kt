package io.ing9990.monitor.aop

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.MultipartFile
import java.lang.reflect.Method
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Aspect
@Component
class ControllerLoggingAspect(
    private val objectMapper: ObjectMapper,
    private val environment: Environment,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val apiMonitorLogger = LoggerFactory.getLogger("api.monitor")
    private val apiMonitoringMarker = MarkerFactory.getMarker("API_MONITORING")

    private val requestMappingAnnotations =
        setOf(
            GetMapping::class.java,
            PostMapping::class.java,
            PutMapping::class.java,
            DeleteMapping::class.java,
            PatchMapping::class.java,
            RequestMapping::class.java,
        )

    @Pointcut(
        "@within(org.springframework.web.bind.annotation.RestController) || " +
            "@within(org.springframework.stereotype.Controller)",
    )
    fun controllerMethods() {
        // Pointcut definition
    }

    @Around("controllerMethods()")
    fun logControllerMethodExecution(joinPoint: ProceedingJoinPoint): Any? {
        val startTime = Instant.now()

        // 요청 정보 로깅 - 안전한 방식으로
        var httpRequest: HttpServletRequest? = null
        var requestInfo: Map<String, Any?> = emptyMap()

        try {
            httpRequest = getHttpRequest()
            if (httpRequest != null) {
                val methodSignature = joinPoint.signature as MethodSignature
                val method = methodSignature.method
                requestInfo = buildRequestInfo(httpRequest, joinPoint, method)

                // 요청 로깅 (일반)
                logger.info("API Request - ${objectMapper.writeValueAsString(requestInfo)}")
            }
        } catch (e: Exception) {
            // 로깅 오류는 무시하고 비즈니스 로직 실행 계속
            logger.error("API 요청 로깅 중 오류 발생 (비즈니스 로직에 영향 없음)", e)
        }

        // 메서드 실행 - 핵심 비즈니스 로직
        val result: Any?
        try {
            result = joinPoint.proceed()

            // 성공 응답 로깅 - 안전한 방식으로
            safeLogSuccess(httpRequest, requestInfo, result, startTime)

            return result
        } catch (e: Exception) {
            // 에러 로깅 - 안전한 방식으로
            safeLogError(httpRequest, requestInfo, e, startTime)

            // 원래 예외를 다시 던져서 비즈니스 로직 흐름 유지
            throw e
        }
    }

    // 성공 응답 로깅을 위한 안전한 메서드
    private fun safeLogSuccess(
        httpRequest: HttpServletRequest?,
        requestInfo: Map<String, Any?>,
        result: Any?,
        startTime: Instant,
    ) {
        try {
            if (httpRequest == null) return

            // 응답 정보 구성
            val responseInfo = buildResponseInfo(result, startTime)

            // 응답 로깅 (일반)
            logger.info("API Response - ${objectMapper.writeValueAsString(responseInfo)}")

            // 실행 시간 계산
            val executionTimeMs = Duration.between(startTime, Instant.now()).toMillis()
            val statusCode = getStatusCode(result)

            // 슬랙 알림용 메시지 구성
            val monitorMessage =
                """
                API 모니터링 알림
                경로: ${httpRequest.method} ${httpRequest.requestURI}${if (httpRequest.queryString != null) "?${httpRequest.queryString}" else ""}
                실행시간: ${executionTimeMs}ms
                IP: ${getClientIp(httpRequest)}
                상태코드: $statusCode
                User-Agent: ${httpRequest.getHeader("User-Agent") ?: "알 수 없음"}
                응답크기: ${getResponseSize(result)}
                """.trimIndent()

            // 전용 로거로 메시지 전송
            apiMonitorLogger.info(apiMonitoringMarker, monitorMessage)
        } catch (e: Exception) {
            // 로깅 실패는 조용히 처리 - 비즈니스 로직에 영향 주지 않음
            logger.error("API 응답 로깅 중 오류 발생 (비즈니스 로직에 영향 없음)", e)
        }
    }

    // 에러 로깅을 위한 안전한 메서드
    private fun safeLogError(
        httpRequest: HttpServletRequest?,
        requestInfo: Map<String, Any?>,
        e: Exception,
        startTime: Instant,
    ) {
        try {
            if (httpRequest == null) return

            // 예외 정보 구성
            val errorInfo =
                mapOf(
                    "request" to requestInfo,
                    "error" to
                        mapOf(
                            "exception" to e.javaClass.name,
                            "message" to (e.message ?: "No message"),
                            "timestamp" to
                                LocalDateTime
                                    .now()
                                    .format(DateTimeFormatter.ISO_DATE_TIME),
                            "executionTimeMs" to
                                Duration
                                    .between(startTime, Instant.now())
                                    .toMillis(),
                        ),
                )

            // 예외 로깅 (일반)
            logger.error("API Error - ${objectMapper.writeValueAsString(errorInfo)}", e)

            // 에러 발생 시 API 모니터링 로깅
            val executionTimeMs = Duration.between(startTime, Instant.now()).toMillis()

            // API_MONITORING 마커와 함께 에러 로깅
            val monitorErrorMessage =
                """
                API 모니터링 알림
                경로: ${httpRequest.method} ${httpRequest.requestURI}${if (httpRequest.queryString != null) "?${httpRequest.queryString}" else ""}
                실행시간: ${executionTimeMs}ms
                IP: ${getClientIp(httpRequest)}
                상태코드: 500
                예외: ${e.javaClass.simpleName}
                에러메시지: ${e.message ?: "알 수 없음"}
                User-Agent: ${httpRequest.getHeader("User-Agent") ?: "알 수 없음"}
                """.trimIndent()

            // 에러 로그 전송 (API_MONITORING 마커 사용)
            apiMonitorLogger.error(apiMonitoringMarker, monitorErrorMessage)

            // 500 에러 전용 로거로도 로깅
            try {
                val error500Logger = LoggerFactory.getLogger("500_ERROR_LOGGER")
                error500Logger.error(
                    "API Error - ${httpRequest.method} ${httpRequest.requestURI} - ${e.message}",
                    e,
                )
            } catch (innerException: Exception) {
                // 중첩 예외 처리 - 무시하고 계속 진행
                logger.error("500 에러 로깅 중 예외 발생", innerException)
            }
        } catch (loggingException: Exception) {
            // 로깅 실패는 조용히 처리 - 비즈니스 로직에 영향 주지 않음
            logger.error("API 에러 로깅 중 오류 발생 (비즈니스 로직에 영향 없음)", loggingException)
        }
    }

    private fun buildRequestInfo(
        request: HttpServletRequest,
        joinPoint: ProceedingJoinPoint,
        method: Method,
    ): Map<String, Any?> {
        try {
            val methodSignature = joinPoint.signature as MethodSignature
            val parameterNames = methodSignature.parameterNames
            val args = joinPoint.args

            // 요청 매개변수 처리 (민감한 정보 필터링)
            val params = mutableMapOf<String, Any?>()
            for (i in parameterNames.indices) {
                if (i < args.size) {
                    val paramValue =
                        when {
                            args[i] == null -> null
                            args[i] is MultipartFile -> "MultipartFile: ${(args[i] as MultipartFile).originalFilename}"
                            args[i] is Collection<*> &&
                                (args[i] as Collection<*>).isNotEmpty() &&
                                (args[i] as Collection<*>).first() is MultipartFile ->
                                "MultipartFiles: ${(args[i] as Collection<*>).size} files"

                            args[i]::class.java.name.contains(
                                "password",
                                ignoreCase = true,
                            ) -> "******"
                            // 인증 토큰, 시크릿 키 등 민감 정보 마스킹
                            parameterNames[i].contains("token", ignoreCase = true) ||
                                parameterNames[i].contains("secret", ignoreCase = true) ||
                                parameterNames[i].contains("key", ignoreCase = true) ||
                                parameterNames[i].contains("auth", ignoreCase = true) -> "******"

                            else ->
                                try {
                                    objectMapper.writeValueAsString(args[i])
                                } catch (e: Exception) {
                                    args[i].toString()
                                }
                        }
                    params[parameterNames[i]] = paramValue
                }
            }

            // 엔드포인트 경로 추출
            val endpointPath = extractEndpointPath(method)

            return mapOf(
                "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "method" to request.method,
                "path" to request.requestURI,
                "endpointPath" to endpointPath,
                "queryString" to (if (request.queryString != null) request.queryString else ""),
                "headers" to getHeadersAsMap(request),
                "parameters" to params,
                "clientIp" to getClientIp(request),
                "profile" to
                    try {
                        environment.activeProfiles.joinToString(",")
                    } catch (e: Exception) {
                        "unknown"
                    },
            )
        } catch (e: Exception) {
            // 요청 정보 구성 중 오류가 발생하면 최소한의 정보만 포함하는 맵 반환
            logger.error("요청 정보 구성 중 오류 발생", e)
            return mapOf(
                "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "method" to request.method,
                "path" to request.requestURI,
                "error" to "Request info build failed: ${e.message}",
            )
        }
    }

    private fun buildResponseInfo(
        result: Any?,
        startTime: Instant,
    ): Map<String, Any?> {
        try {
            val endTime = Instant.now()
            val executionTime = Duration.between(startTime, endTime).toMillis()

            val responseData =
                when {
                    result == null -> null
                    result is ResponseEntity<*> ->
                        mapOf(
                            "statusCode" to result.statusCode.value(),
                            "body" to result.body,
                        )

                    else -> result
                }

            return mapOf(
                "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "executionTimeMs" to executionTime,
                "data" to responseData,
            )
        } catch (e: Exception) {
            // 응답 정보 구성 중 오류가 발생하면 최소한의 정보만 포함하는 맵 반환
            logger.error("응답 정보 구성 중 오류 발생", e)
            return mapOf(
                "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "executionTimeMs" to Duration.between(startTime, Instant.now()).toMillis(),
                "error" to "Response info build failed: ${e.message}",
            )
        }
    }

    private fun getHeadersAsMap(request: HttpServletRequest): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        try {
            val headerNames = request.headerNames

            while (headerNames.hasMoreElements()) {
                val headerName = headerNames.nextElement()
                // 인증 관련 헤더는 마스킹 처리
                val headerValue =
                    when {
                        headerName.equals("authorization", ignoreCase = true) -> "******"
                        headerName.equals("cookie", ignoreCase = true) -> "******"
                        headerName.contains("token", ignoreCase = true) -> "******"
                        headerName.contains("key", ignoreCase = true) -> "******"
                        headerName.contains("secret", ignoreCase = true) -> "******"
                        else -> request.getHeader(headerName)
                    }
                headers[headerName] = headerValue
            }
        } catch (e: Exception) {
            logger.error("헤더 정보 추출 중 오류 발생", e)
            headers["error"] = "Headers extraction failed: ${e.message}"
        }

        return headers
    }

    private fun getClientIp(request: HttpServletRequest): String {
        try {
            var clientIp = request.getHeader("X-Forwarded-For")

            if (clientIp.isNullOrEmpty() || "unknown".equals(clientIp, ignoreCase = true)) {
                clientIp = request.getHeader("Proxy-Client-IP")
            }
            if (clientIp.isNullOrEmpty() || "unknown".equals(clientIp, ignoreCase = true)) {
                clientIp = request.getHeader("WL-Proxy-Client-IP")
            }
            if (clientIp.isNullOrEmpty() || "unknown".equals(clientIp, ignoreCase = true)) {
                clientIp = request.getHeader("HTTP_CLIENT_IP")
            }
            if (clientIp.isNullOrEmpty() || "unknown".equals(clientIp, ignoreCase = true)) {
                clientIp = request.getHeader("HTTP_X_FORWARDED_FOR")
            }
            if (clientIp.isNullOrEmpty() || "unknown".equals(clientIp, ignoreCase = true)) {
                clientIp = request.remoteAddr
            }

            return clientIp ?: "unknown"
        } catch (e: Exception) {
            logger.error("클라이언트 IP 추출 중 오류 발생", e)
            return "error-getting-ip"
        }
    }

    private fun extractEndpointPath(method: Method): String {
        try {
            for (annotationType in requestMappingAnnotations) {
                val annotation = method.getAnnotation(annotationType)
                if (annotation != null) {
                    val value =
                        when (annotation) {
                            is GetMapping -> annotation.value
                            is PostMapping -> annotation.value
                            is PutMapping -> annotation.value
                            is DeleteMapping -> annotation.value
                            is PatchMapping -> annotation.value
                            is RequestMapping -> annotation.value
                            else -> emptyArray()
                        }

                    if (value.isNotEmpty()) {
                        return value[0]
                    }

                    val path =
                        when (annotation) {
                            is GetMapping -> annotation.path
                            is PostMapping -> annotation.path
                            is PutMapping -> annotation.path
                            is DeleteMapping -> annotation.path
                            is PatchMapping -> annotation.path
                            is RequestMapping -> annotation.path
                            else -> emptyArray()
                        }

                    if (path.isNotEmpty()) {
                        return path[0]
                    }
                }
            }

            // 컨트롤러 클래스 레벨의 패스 확인
            val controllerClass = method.declaringClass
            val requestMappingOnClass = controllerClass.getAnnotation(RequestMapping::class.java)

            return if (requestMappingOnClass != null && requestMappingOnClass.value.isNotEmpty()) {
                requestMappingOnClass.value[0]
            } else if (requestMappingOnClass != null && requestMappingOnClass.path.isNotEmpty()) {
                requestMappingOnClass.path[0]
            } else {
                "Unknown path"
            }
        } catch (e: Exception) {
            logger.error("엔드포인트 경로 추출 중 오류 발생", e)
            return "error-getting-path"
        }
    }

    private fun getStatusCode(result: Any?): Int =
        try {
            when (result) {
                is ResponseEntity<*> -> result.statusCode.value()
                else -> 200 // 기본값
            }
        } catch (e: Exception) {
            logger.error("상태 코드 추출 중 오류 발생", e)
            200 // 오류 발생 시 기본값 반환
        }

    private fun getHttpRequest(): HttpServletRequest? =
        try {
            val requestAttributes = RequestContextHolder.getRequestAttributes()
            if (requestAttributes is ServletRequestAttributes) {
                requestAttributes.request
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("HTTP 요청 객체 가져오기 중 오류 발생", e)
            null
        }

    /**
     * 응답 데이터의 크기를 대략적으로 계산하는 함수
     */
    private fun getResponseSize(result: Any?): String =
        try {
            when {
                result == null -> "0 bytes"
                result is ResponseEntity<*> -> {
                    val body = result.body
                    when {
                        body == null -> "0 bytes"
                        body is ByteArray -> "${body.size} bytes"
                        body is String -> "${body.length} 문자"
                        else ->
                            try {
                                val json = objectMapper.writeValueAsString(body)
                                "${json.length} bytes (JSON)"
                            } catch (e: Exception) {
                                "계산 불가"
                            }
                    }
                }

                else ->
                    try {
                        val json = objectMapper.writeValueAsString(result)
                        "${json.length} bytes (JSON)"
                    } catch (e: Exception) {
                        "계산 불가"
                    }
            }
        } catch (e: Exception) {
            logger.error("응답 크기 계산 중 오류 발생", e)
            "계산 불가 (오류)"
        }
}
