package io.ing9990.api.search

/**
 * 검색 제안 응답을 위한 DTO
 */
data class SearchSuggestionResponse(
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val imageUrl: String,
)
