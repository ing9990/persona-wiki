package io.ing9990.admin.service

import io.ing9990.admin.repository.AdminCategoryRepository
import io.ing9990.domain.figure.Category
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminCategoryService(
    private val categoryRepository: AdminCategoryRepository,
) {
    /**
     * 모든 카테고리를 조회한다.
     */
    fun getAllCategories(): List<Category> {
        return categoryRepository.findAll()
    }

    /**
     * 카테고리를 페이징하여 조회한다.
     */
    fun getCategoriesPaged(pageable: Pageable): Page<Category> {
        return categoryRepository.findAll(pageable)
    }

    /**
     * 카테고리 ID로 카테고리를 조회한다.
     */
    fun getCategoryById(id: String): Category? {
        return categoryRepository.findById(id).orElse(null)
    }

    /**
     * 카테고리 개수를 조회한다.
     */
    fun getCategoryCount(): Long {
        return categoryRepository.count()
    }

    /**
     * 새 카테고리를 생성한다.
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
     * 카테고리를 수정한다.
     */
    @Transactional
    fun updateCategory(
        id: String,
        displayName: String,
        description: String?,
        imageUrl: String?,
    ): Category {
        val category =
            categoryRepository.findById(id)
                .orElseThrow { IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다: $id") }

        // 다른 카테고리와 표시 이름 중복 체크
        val existingWithDisplayName = categoryRepository.findByDisplayName(displayName)
        if (existingWithDisplayName != null && existingWithDisplayName.id != id) {
            throw IllegalArgumentException("이미 존재하는 카테고리 표시 이름입니다: $displayName")
        }

        // 새 Category 객체를 생성하여 변경 적용
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
     * 카테고리를 삭제한다.
     */
    @Transactional
    fun deleteCategory(id: String) {
        val category =
            categoryRepository.findById(id)
                .orElseThrow { IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다: $id") }

        if (category.figures.isNotEmpty()) {
            throw IllegalStateException("해당 카테고리에 속한 인물이 있어 삭제할 수 없습니다: $id")
        }

        categoryRepository.delete(category)
    }
}
