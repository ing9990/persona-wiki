package io.ing9990.api.figures.dto.request

data class CreateFigureRequest(
    val name: String,
    val categoryId: String,
    val imageUrl: String?,
    val bio: String?,
)
