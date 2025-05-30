package io.ing9990.api.search

import io.ing9990.domain.EntityNotFoundException
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.figure.service.dto.FigureCardResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 검색 관련 REST API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api")
class SearchApi(
    private val figureService: FigureService,
) {
    @GetMapping("/search/suggestions")
    fun getSearchSuggestions(
        @RequestParam query: String,
    ): ResponseEntity<List<SearchSuggestionResponse>> {
        val results: List<FigureCardResult> =
            figureService.searchByName(query)

        val suggestions =
            results.take(5).map { figure ->
                SearchSuggestionResponse(
                    name = figure.name,
                    categoryId = figure.categoryId,
                    categoryName = figure.categoryName,
                    imageUrl = figure.image,
                    slug = figure.slug,
                )
            }

        return ResponseEntity.ok(suggestions)
    }

    @GetMapping("/search/present-validation")
    fun isPresent(
        @RequestParam(required = true) categoryId: String,
        @RequestParam(required = true) slug: String,
    ): ResponseEntity<FigureCardResult> {
        try {
            val result: FigureCardResult =
                figureService.searchByCategoryIdAndNameOrNull(
                    categoryId,
                    slug,
                )

            return ResponseEntity.status(HttpStatus.OK).body(result)
        } catch (e: EntityNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }
}
