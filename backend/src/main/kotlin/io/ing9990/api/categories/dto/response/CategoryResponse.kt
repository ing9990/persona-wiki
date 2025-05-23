package io.ing9990.api.categories.dto.response

import io.ing9990.domain.figure.Category

data class CategoryResponse(
    val id: String,
    val displayName: String,
    val description: String?,
    val imageUrl: String?,
) {
    companion object {
        fun from(category: Category): CategoryResponse {
            return CategoryResponse(
                id = category.id,
                displayName = category.displayName,
                description = category.description,
                imageUrl = category.imageUrl,
            )
        }
    }
}
