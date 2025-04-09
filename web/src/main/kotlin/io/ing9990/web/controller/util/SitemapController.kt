// web/src/main/kotlin/io/ing9990/web/controller/SitemapController.kt
package io.ing9990.web.controller.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SitemapController {
    @GetMapping("/sitemap.xml", produces = [MediaType.APPLICATION_XML_VALUE])
    @ResponseBody
    fun getSitemap(request: HttpServletRequest): String = "/sitemap.xml"

    private fun getBaseUrl(request: HttpServletRequest): String {
        val scheme = request.scheme
        val serverName = request.serverName
        val serverPort = request.serverPort
        val contextPath = request.contextPath

        val url = StringBuilder()
        url.append(scheme).append("://").append(serverName)

        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort)
        }

        url.append(contextPath)

        return url.toString()
    }
}
