package io.ing9990.web.controller

import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.figure.service.FigureService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Controller
class SiteMap(
    private val categoryService: CategoryService,
    private val figureService: FigureService,
) {
    @GetMapping("/sitemap.xml", produces = [MediaType.APPLICATION_XML_VALUE])
    @ResponseBody
    fun getSitemap(request: HttpServletRequest): String {
        val baseUrl = getBaseUrl(request)
        val currentDate = formatDate(LocalDateTime.now())

        val stringBuilder = StringBuilder()
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        stringBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n")

        // 메인 페이지
        appendUrl(stringBuilder, baseUrl, currentDate, "daily", "1.0")

        // 인물 목록 페이지
        appendUrl(stringBuilder, "$baseUrl/figures", currentDate, "daily", "0.9")

        // 카테고리 목록 페이지
        appendUrl(stringBuilder, "$baseUrl/categories", currentDate, "weekly", "0.8")

        // 검색 페이지
        appendUrl(stringBuilder, "$baseUrl/search", currentDate, "weekly", "0.7")

        // 약관 및 개인정보처리방침
        appendUrl(stringBuilder, "$baseUrl/terms", currentDate, "monthly", "0.5")
        appendUrl(stringBuilder, "$baseUrl/privacy", currentDate, "monthly", "0.5")

        // 활동 페이지
        appendUrl(stringBuilder, "$baseUrl/activity", currentDate, "daily", "0.6")

        // 카테고리 상세 페이지
        val categories = categoryService.getAllCategories()
        categories.forEach { category ->
            appendUrl(
                stringBuilder,
                "$baseUrl/categories/${category.id}",
                currentDate,
                "weekly",
                "0.7",
            )

            // 해당 카테고리의 인물 상세 페이지
            val figures = figureService.findByCategoryId(category.id)
            figures.forEach { figure ->
                appendUrl(
                    stringBuilder,
                    "$baseUrl/${category.id}/@${figure.name}",
                    currentDate,
                    "weekly",
                    "0.6",
                )
            }
        }

        stringBuilder.append("</urlset>")
        return stringBuilder.toString()
    }

    private fun formatDate(date: LocalDateTime): String = date.atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private fun appendUrl(
        sb: StringBuilder,
        url: String,
        lastmod: String,
        changefreq: String,
        priority: String,
    ) {
        sb.append("  <url>\n")
        sb.append("    <loc>$url</loc>\n")
        sb.append("    <lastmod>$lastmod</lastmod>\n")
        sb.append("    <changefreq>$changefreq</changefreq>\n")
        sb.append("    <priority>$priority</priority>\n")
        sb.append("  </url>\n")
    }

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
