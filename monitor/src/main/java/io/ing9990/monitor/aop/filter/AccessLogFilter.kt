package io.ing9990.monitor.aop.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class AccessLogFilter : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(AccessLogFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val requestId = UUID.randomUUID().toString()
            val startTime = Instant.now()
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            val clientIp = getClientIp(request)
            val method = request.method
            val uri = request.requestURI
            val queryString = request.queryString ?: ""
            val userAgent = request.getHeader("User-Agent") ?: "Unknown"

            logger.info(
                "ACCESS START | ID: $requestId | Time: $timestamp | IP: $clientIp | $method $uri${
                    if (queryString.isNotEmpty()) {
                        "" + "?$queryString"
                    } else {
                        ""
                    }
                } | UA: $userAgent",
            )

            try {
                // 요청 ID를 응답 헤더에 추가 (디버깅 용이성을 위해)
                response.addHeader("X-Request-ID", requestId)

                // 다음 필터 또는 컨트롤러로 요청 전달
                filterChain.doFilter(request, response)
            } catch (e: Exception) {
                e.printStackTrace()

                return
            } finally {
                // 요청 종료 시간 및 소요 시간 계산
                val endTime = Instant.now()
                val duration = Duration.between(startTime, endTime).toMillis()
                val statusCode = response.status

                // 요청 종료 로깅
                logger.info(
                    "ACCESS END | ID: $requestId | Time: ${
                        LocalDateTime.now().format(
                            DateTimeFormatter.ISO_DATE_TIME,
                        )
                    } | Duration: ${
                        duration
                    }ms | Status: $statusCode | $method $uri${
                        if (queryString.isNotEmpty()) "?$queryString" else ""
                    }",
                )
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun getClientIp(request: HttpServletRequest): String {
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
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI

        return path.startsWith("/favicon.ico") ||
            path.startsWith("/static/") ||
            path.startsWith("/resources/") ||
            path.startsWith(
                "/assets/",
            ) ||
            path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.startsWith("/images/") ||
            path.startsWith(
                "/actuator/health",
            )
    }
}
