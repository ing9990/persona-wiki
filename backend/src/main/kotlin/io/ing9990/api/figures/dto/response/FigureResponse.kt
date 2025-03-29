package io.ing9990.api.figures.dto.response

import io.ing9990.domain.figure.Figure

data class FigureResponse(
    val id: Long?,
    val name: String,
    val categoryId: String,
    val categoryName: String,
    val imageUrl: String?,
    val bio: String?,
) {
    companion object {
        fun from(figure: Figure): FigureResponse {
            return FigureResponse(
                id = figure.id,
                name = figure.name,
                categoryId = figure.category.id,
                categoryName = figure.category.displayName,
                imageUrl = figure.imageUrl,
                bio = figure.bio,
            )
        }
    }
}
