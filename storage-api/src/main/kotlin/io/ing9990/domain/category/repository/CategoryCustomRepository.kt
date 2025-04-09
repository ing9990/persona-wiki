package io.ing9990.domain.category.repository

import io.ing9990.domain.figure.service.dto.PopularFiguresByCategoriesResult

interface CategoryCustomRepository {
    fun getPopularFiguresByCategoryTop(
        topCategoriesCount: Long,
        figuresCount: Long,
    ): PopularFiguresByCategoriesResult
}
