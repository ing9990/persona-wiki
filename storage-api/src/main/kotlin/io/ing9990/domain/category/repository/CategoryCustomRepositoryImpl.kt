package io.ing9990.domain.category.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.category.Category
import io.ing9990.domain.category.querydsl.QCategory
import io.ing9990.domain.category.service.dto.CategoryResult
import io.ing9990.domain.figure.querydsl.QFigure
import io.ing9990.domain.figure.service.dto.FigureCardResult
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
            topCategories.map { foundCategory ->
                // 카테고리의 총 인물 개수 조회
                val totalFiguresCount =
                    query
                        .select(figure.count())
                        .from(figure)
                        .where(figure.category.id.eq(foundCategory.id))
                        .fetchOne() ?: 0L

                // 해당 카테고리의 인기 인물(투표 수 기준) 조회
                val popularFigures =
                    query
                        .selectFrom(figure)
                        .where(figure.category.id.eq(foundCategory.id))
                        .orderBy(figure.votes.size().desc())
                        .limit(figuresCount)
                        .fetch()
                        .map {
                            FigureCardResult.from(it)
                        }

                FiguresByCategoryResult(
                    category = CategoryResult.from(foundCategory),
                    figures = popularFigures,
                    totalFigures = totalFiguresCount.toInt(),
                )
            }

        return PopularFiguresByCategoriesResult(datas = result)
    }
}
