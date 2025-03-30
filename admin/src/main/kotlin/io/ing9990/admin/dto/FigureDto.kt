package io.ing9990.admin.dto

import jakarta.validation.constraints.NotBlank

data class FigureDto(
    val id: Long? = null,
    @field:NotBlank(message = "인물 이름은 필수입니다.")
    val name: String = "",
    @field:NotBlank(message = "카테고리는 필수입니다.")
    val categoryId: String = "",
    val imageUrl: String? = null,
    val bio: String? = null,
)
