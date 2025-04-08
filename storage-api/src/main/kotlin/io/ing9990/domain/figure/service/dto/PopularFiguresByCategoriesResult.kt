package io.ing9990.domain.figure.service.dto

import io.ing9990.domain.category.Category

data class PopularFiguresByCategoriesResult(
    var figures: List<FiguresByCategoryResult>,
)

data class FiguresByCategoryResult(
    var category: Category,
    var figures: List<FigureCardResult>,
) {
    fun categoryName() = category.displayName
}
