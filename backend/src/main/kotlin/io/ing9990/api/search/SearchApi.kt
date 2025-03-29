package io.ing9990.api.search

import io.ing9990.domain.figure.service.FigureService
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
    private val figureService: FigureService
) {

    @GetMapping("/search/suggestions")
    fun getSearchSuggestions(@RequestParam query: String): ResponseEntity<List<SearchSuggestion>> {
        // 초성 검색을 위한 처리
        val searchResults = if (query.length <= 3) {
            figureService.searchByNameWithInitials(query)
        } else {
            figureService.searchByName(query)
        }

        // 최대 10개만 반환
        val suggestions = searchResults.take(10).map { figure ->
            SearchSuggestion(
                id = figure.id ?: 0,
                name = figure.name,
                categoryId = figure.category.id,
                categoryName = figure.category.displayName,
                imageUrl = figure.imageUrl ?: ""
            )
        }

        return ResponseEntity.ok(suggestions)
    }
}