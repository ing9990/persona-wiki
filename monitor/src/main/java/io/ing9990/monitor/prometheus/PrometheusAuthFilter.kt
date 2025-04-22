package io.ing9990.monitor.prometheus

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Base64

@Component
class PrometheusAuthFilter(
    @Value("\${auth.username}")
    private var username: String,
    @Value("\${auth.password}")
    private var password: String,
) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(PrometheusAuthFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        logger.info("Filter called for URI: ${request.requestURI}")

        if (request.requestURI.endsWith("/actuator/prometheus")) {
            val authHeader = request.getHeader("Authorization")

            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                unauthorized(response)
                return
            }

            try {
                val base64Credentials = authHeader.substring("Basic ".length).trim()
                val credentials = String(Base64.getDecoder().decode(base64Credentials)).split(":")

                if (credentials.size != 2 || credentials[0] != username || credentials[1] != password) {
                    unauthorized(response)
                    return
                }
            } catch (e: Exception) {
                logger.error("Authentication error", e)
                unauthorized(response)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun unauthorized(response: HttpServletResponse) {
        response.setHeader("WWW-Authenticate", "Basic realm=\"Prometheus Metrics\"")
        response.status = HttpServletResponse.SC_UNAUTHORIZED
    }
}
