package io.ing9990.admin.dto

import io.ing9990.domain.figure.Figure
import java.time.LocalDateTime

data class FigureListDto(
    val id: Long?,
    val name: String,
    val imageUrl: String?,
    val bio: String?,
    val categoryId: String,
    val categoryName: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(figure: Figure): FigureListDto {
            return FigureListDto(
                id = figure.id,
                name = figure.name,
                imageUrl = figure.imageUrl,
                bio = figure.bio,
                categoryId = figure.category.id,
                categoryName = figure.category.displayName,
                createdAt = figure.createdAt,
            )
        }
    }
}
