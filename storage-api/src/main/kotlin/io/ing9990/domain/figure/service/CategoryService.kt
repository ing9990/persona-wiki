package io.ing9990.domain.figure.service

import io.ing9990.domain.figure.Category
import io.ing9990.domain.figure.repository.CategoryRepository
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
    fun getAllCategories(): List<Category> {
        return categoryRepository.findAll()
    }

    /**
     * ID로 카테고리 조회
     */
    fun getCategoryById(id: String): Category? {
        return categoryRepository.findById(id).orElse(null)
    }

    /**
     * 표시 이름으로 카테고리 조회
     */
    fun getCategoryByDisplayName(displayName: String): Category? {
        return categoryRepository.findByDisplayName(displayName)
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
        // ID 중복 체크
        if (categoryRepository.existsById(id)) {
            throw IllegalArgumentException("이미 존재하는 카테고리 ID입니다: $id")
        }

        // 표시 이름 중복 체크
        if (categoryRepository.existsByDisplayName(displayName)) {
            throw IllegalArgumentException("이미 존재하는 카테고리 표시 이름입니다: $displayName")
        }

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
     * 카테고리 수정
     */
    @Transactional
    fun updateCategory(
        id: String,
        displayName: String,
        description: String?,
        imageUrl: String?,
    ): Category {
        val category =
            getCategoryById(id)
                ?: throw IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다: $id")

        // 다른 카테고리와 표시 이름 중복 체크
        val existingWithDisplayName = getCategoryByDisplayName(displayName)
        if (existingWithDisplayName != null && existingWithDisplayName.id != id) {
            throw IllegalArgumentException("이미 존재하는 카테고리 표시 이름입니다: $displayName")
        }

        // 새 Category 객체를 생성하여 변경 적용 (Kotlin에서는, val 필드를 변경할 수 없으므로 새 객체 생성)
        val updatedCategory =
            Category(
                id = id,
                displayName = displayName,
                description = description,
                imageUrl = imageUrl,
            )

        // 기존 figures 유지
        category.figures.forEach { figure ->
            updatedCategory.addFigure(figure)
        }

        return categoryRepository.save(updatedCategory)
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    fun deleteCategory(id: String) {
        val category =
            getCategoryById(id)
                ?: throw IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다: $id")

        if (category.figures.isNotEmpty()) {
            throw IllegalStateException("해당 카테고리에 속한 인물이 있어 삭제할 수 없습니다: $id")
        }

        categoryRepository.delete(category)
    }
}
