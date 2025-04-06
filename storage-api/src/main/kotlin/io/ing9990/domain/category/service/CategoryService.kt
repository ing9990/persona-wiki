package io.ing9990.domain.category.service

import io.ing9990.domain.EntityNotFoundException
import io.ing9990.domain.category.Category
import io.ing9990.domain.category.repository.CategoryRepository
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
    fun getAllCategories(): List<Category> = categoryRepository.findAll()

    /**
     * ID로 카테고리 조회
     */
    fun getCategoryById(id: String): Category =
        categoryRepository.findById(id).orElseThrow {
            EntityNotFoundException("Category", id, "해당 ID의 카테고리가 존재하지 않습니다: $id")
        }

    /**
     * 새 카테고리 생성
     */
    @Transactional
    fun createCategory(
        id: String,
        displayName: String,
        description: String?,
        imageUrl: String?,
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
