package io.ing9990.domain.category.service

import io.ing9990.domain.EntityNotFoundException
import io.ing9990.domain.category.Category
import io.ing9990.domain.category.repository.CategoryRepository
import io.ing9990.domain.category.service.dto.CategoryIds
import io.ing9990.domain.category.service.dto.CategoryResult
import io.ing9990.domain.figure.service.dto.PopularFiguresByCategoriesResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 카테고리 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {
    /**
     * 전체 카테고리 조회
     */
    fun getAllCategories(): List<CategoryResult> =
        categoryRepository
            .findAll()
            .map {
                CategoryResult.from(it)
            }

    /**
     * 특정 카테고리를 제외하고 카테고리를 N개 만큼 조회
     */
    fun getAllCategoriesWithNotIn(
        exception: CategoryIds,
        limit: Int,
    ): List<CategoryResult> =
        getAllCategories()
            .filter { category -> category.id !in exception.categories }
            .take(limit)

    /**
     * ID로 카테고리 조회
     */
    fun getCategoryById(id: String): Category =
        categoryRepository.findById(id).orElseThrow {
            EntityNotFoundException("Category", id, "해당 ID의 카테고리가 존재하지 않습니다: $id")
        }

    /**
     * 인물이 많은 카테고리를 상위 (topCount) 개 만큼 가져옵니다.
     * 각 카테고리에서 투표 수가 가장 많은 인물을 (figuresCount)개 만큼 가져옵니다.
     */
    @Transactional(readOnly = true)
    fun getPopularFiguresByCategory(
        topCount: Long = 5L,
        figuresCount: Long = 3L,
    ): PopularFiguresByCategoriesResult {
        val topCategories =
            categoryRepository
                .getPopularFiguresByCategoryTop(topCount, figuresCount)

        return topCategories
    }

    /**
     * 새 카테고리 생성
     */
    @Transactional
    fun createCategory(
        id: String,
        displayName: String,
        description: String,
        imageUrl: String,
    ): Category {
        validateCreateCategory(id, displayName)

        val category =
            Category(
                id = id,
                displayName = displayName,
                description = description,
                imageUrl = imageUrl,
            )

        return categoryRepository.save(category)
    }

    /**
     * 카테고리 ID로 카테고리 정보를 조회합니다.
     * @param categoryId, 카테고리 ID
     */
    fun findCategoryById(categoryId: String): Category =
        categoryRepository.findById(categoryId).orElse(null)
            ?: throw IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다: $categoryId")

    private fun validateCreateCategory(
        id: String,
        displayName: String,
    ) {
        if (categoryRepository.existsById(id)) {
            throw IllegalArgumentException("이미 존재하는 카테고리 ID입니다: $id")
        }

        if (categoryRepository.existsByDisplayName(displayName)) {
            throw IllegalArgumentException("이미 존재하는 카테고리 표시 이름입니다: $displayName")
        }
    }
}
