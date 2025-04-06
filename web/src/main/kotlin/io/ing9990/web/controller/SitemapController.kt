// web/src/main/kotlin/io/ing9990/web/controller/SitemapController.kt
package io.ing9990.web.controller

import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.figure.service.FigureService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.format.DateTimeFormatter

@Controller
class SitemapController(
    private val figureService: FigureService,
    private val categoryService: CategoryService,
) {
    @GetMapping("/sitemap.xml", produces = [MediaType.APPLICATION_XML_VALUE])
    @ResponseBody
    fun getSitemap(request: HttpServletRequest): String {
        val baseUrl = getBaseUrl(request)
        val figures = figureService.findAllWithCategorySITEMAP()
        val categories = categoryService.getAllCategories()

        val dateFormatter = DateTimeFormatter.ISO_INSTANT

        val sitemap = StringBuilder()
        sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n")

        // 기본 페이지
        sitemap.append("  <url>\n")
        sitemap.append("    <loc>$baseUrl</loc>\n")
        sitemap.append("    <changefreq>daily</changefreq>\n")
        sitemap.append("    <priority>1.0</priority>\n")
        sitemap.append("  </url>\n")

        // 인물 목록 페이지
        sitemap.append("  <url>\n")
        sitemap.append("    <loc>$baseUrl/figures</loc>\n")
        sitemap.append("    <changefreq>daily</changefreq>\n")
        sitemap.append("    <priority>0.9</priority>\n")
        sitemap.append("  </url>\n")

        // 카테고리 목록 페이지
        sitemap.append("  <url>\n")
        sitemap.append("    <loc>$baseUrl/categories</loc>\n")
        sitemap.append("    <changefreq>weekly</changefreq>\n")
        sitemap.append("    <priority>0.8</priority>\n")
        sitemap.append("  </url>\n")

        // 카테고리 상세 페이지
        for (category in categories) {
            sitemap.append("  <url>\n")
            sitemap.append("    <loc>$baseUrl/categories/${category.id}</loc>\n")
            sitemap.append("    <lastmod>${category.updatedAt.format(dateFormatter)}</lastmod>\n")
            sitemap.append("    <changefreq>weekly</changefreq>\n")
            sitemap.append("    <priority>0.7</priority>\n")
            sitemap.append("  </url>\n")
        }

        // 인물 상세 페이지
        for (figure in figures) {
            sitemap.append("  <url>\n")
            sitemap.append("    <loc>$baseUrl/${figure.category.id}/@${figure.name}</loc>\n")
            sitemap.append("    <lastmod>${figure.updatedAt.format(dateFormatter)}</lastmod>\n")
            sitemap.append("    <changefreq>weekly</changefreq>\n")
            sitemap.append("    <priority>0.6</priority>\n")
            sitemap.append("  </url>\n")
        }

        sitemap.append("</urlset>")

        return sitemap.toString()
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
