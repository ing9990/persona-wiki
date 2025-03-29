package io.ing9990.admin.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CategoryDto(
    @field:NotBlank(message = "카테고리 ID는 필수입니다.")
    @field:Pattern(regexp = "^[a-z0-9-]+$", message = "카테고리 ID는 영문 소문자, 숫자, 하이픈만 사용 가능합니다.")
    val id: String = "",
    @field:NotBlank(message = "표시 이름은 필수입니다.")
    val displayName: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
)
