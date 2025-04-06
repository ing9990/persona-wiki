package io.ing9990.domain.category.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.category.Category
import io.ing9990.domain.category.querydsl.QCategory
import io.ing9990.domain.figure.querydsl.QFigure
import io.ing9990.domain.figure.service.dto.FiguresByCategoryResult
import io.ing9990.domain.figure.service.dto.PopularFiguresByCategoriesResult

class CategoryCustomRepositoryImpl(
    private val query: JPAQueryFactory,
) : CategoryCustomRepository {
    /**
     * Vote가 많은 인물을 카테고리 별로 조회합니다.
     */
    override fun getPopularFiguresByCategoryTop(
        topCount: Long,
        figuresCount: Long,
    ): PopularFiguresByCategoriesResult {
        val figure = QFigure.figure
        val category = QCategory.category

        val topCategories: List<Category> =
            query
                .selectFrom(category)
                .orderBy(category.figures.size().desc())
                .limit(topCount)
                .fetch()

        val result =
            topCategories.map { category ->
                FiguresByCategoryResult(
                    category,
                    query
                        .selectFrom(figure)
                        .where(figure.category.eq(category))
                        .orderBy(figure.votes.size().desc())
                        .limit(figuresCount)
                        .fetch(),
                )
            }

        return PopularFiguresByCategoriesResult(figures = result)
    }
}
