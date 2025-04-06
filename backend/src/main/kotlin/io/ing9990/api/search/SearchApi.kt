package io.ing9990.api.search

import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.figure.service.dto.FigureMicroResult
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
    ): ResponseEntity<List<SearchSuggestion>> {
        val searchResults: List<FigureMicroResult> =
            figureService.searchByName(query)

        val suggestions =
            searchResults.take(5).map { figure ->
                SearchSuggestion(
                    name = figure.figureName,
                    categoryId = figure.categoryId,
                    categoryName = figure.categoryName,
                    imageUrl = figure.figureImage,
                )
            }

        return ResponseEntity.ok(suggestions)
    }
}
