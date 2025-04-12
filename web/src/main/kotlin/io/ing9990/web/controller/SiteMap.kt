package io.ing9990.web.controller

import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.service.UserService
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
    private val userService: UserService,
) {
    @GetMapping("/sitemap.xml", produces = [MediaType.APPLICATION_XML_VALUE])
    @ResponseBody
    fun getSitemap(request: HttpServletRequest): String {
        val baseUrl = getBaseUrl(request)
        val currentDate = formatDate(LocalDateTime.now())

        val stringBuilder = StringBuilder()
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        stringBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" ")
        stringBuilder.append("xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\" ")
        stringBuilder.append("xmlns:news=\"http://www.google.com/schemas/sitemap-news/0.9\" ")
        stringBuilder.append("xmlns:xhtml=\"http://www.w3.org/1999/xhtml\">\n")

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

        // 내 정보 페이지 (로그인 페이지로 리디렉션되므로 포함)
        appendUrl(stringBuilder, "$baseUrl/me", currentDate, "weekly", "0.6")

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
                // slug를 사용한 URL 형식으로 변경
                appendUrl(
                    stringBuilder,
                    "$baseUrl/${category.id}/@${figure.slug}",
                    formatDate(LocalDateTime.now()),
                    "weekly",
                    "0.8",
                    figure.image,
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
        imageUrl: String? = null,
    ) {
        sb.append("  <url>\n")
        sb.append("    <loc>$url</loc>\n")
        sb.append("    <lastmod>$lastmod</lastmod>\n")
        sb.append("    <changefreq>$changefreq</changefreq>\n")
        sb.append("    <priority>$priority</priority>\n")

        // 이미지가 있는 경우 이미지 정보 추가
        if (!imageUrl.isNullOrEmpty()) {
            sb.append("    <image:image>\n")
            sb.append("      <image:loc>$imageUrl</image:loc>\n")
            sb.append("    </image:image>\n")
        }

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
