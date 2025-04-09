package io.ing9990.domain.figure.service.dto

import io.ing9990.domain.category.service.dto.CategoryResult

data class PopularFiguresByCategoriesResult(
    var datas: List<FiguresByCategoryResult>,
)

data class FiguresByCategoryResult(
    var category: CategoryResult,
    var figures: List<FigureCardResult>,
) {
    fun categoryId() = category.id

    fun categoryName() = category.name

    fun getCards(): List<FigureCardResult> = figures
}
