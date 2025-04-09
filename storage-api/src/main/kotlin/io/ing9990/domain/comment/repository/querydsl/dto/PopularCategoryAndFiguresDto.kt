package io.ing9990.domain.comment.repository.querydsl.dto

import io.ing9990.domain.category.Category
import io.ing9990.domain.figure.Figure

/**
 * 카테고리 별 인물
 */
data class PopularCategoryAndFiguresDto(
    val category: Category,
    val figures: List<Figure>,
)
