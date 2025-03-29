package io.ing9990.api.categories.dto.request

data class CategoryRequest(
    val id: String,
    val displayName: String,
    val description: String?,
    val imageUrl: String?,
)
