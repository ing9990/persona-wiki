package io.ing9990.api.category.dto.request

import io.ing9990.domain.category.service.dto.CategoryData
import jakarta.validation.constraints.NotBlank

data class CreateCategoryRequest(
    @NotBlank(message = "카테고리의 ID를 입력하세요.")
    val categoryId: String,
    @NotBlank(message = "카테고리 이름을 입력하세요.")
    val displayName: String,
    @NotBlank(message = "설명을 입력하세요.")
    val description: String,
    @NotBlank
    val imageUrl: String,
) {
    fun toData(): CategoryData = CategoryData(categoryId, displayName, description, imageUrl)
}
