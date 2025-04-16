package io.ing9990.domain.category.service.dto

data class CreateCategoryData(
    val id: String,
    val displayName: String,
    val description: String,
    val imageUrl: String,
)
