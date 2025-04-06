package io.ing9990.domain.figure.service.dto

import io.ing9990.domain.category.Category
import io.ing9990.domain.figure.Figure

data class PopularFiguresByCategoriesResult(
    var figures: List<FiguresByCategoryResult>,
)

data class FiguresByCategoryResult(
    var categories: Category,
    var figures: List<Figure>,
)
