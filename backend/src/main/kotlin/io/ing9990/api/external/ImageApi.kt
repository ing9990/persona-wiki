package io.ing9990.api.external

import io.ing9990.googleimageapi.ImageSearchService
import io.ing9990.wikipidiaapi.clients.dto.response.info.WikipediaInfo
import io.ing9990.wikipidiaapi.service.WikipediaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/search-images")
class ImageApi(
    val imageSearchService: ImageSearchService,
    val wikipediaService: WikipediaService,
) {
    @GetMapping("/wikipedia")
    fun findFigureImage(
        @RequestParam(required = true, name = "figureName") name: String,
    ): WikipediaInfo {
        val basicInfo = wikipediaService.getBasicInfo(name)
        return basicInfo
    }
}
