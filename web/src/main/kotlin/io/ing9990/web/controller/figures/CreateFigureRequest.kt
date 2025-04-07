package io.ing9990.web.controller.figures

import io.ing9990.domain.figure.service.dto.CreateFiureData
import io.ing9990.domain.user.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateFigureRequest(
    @field:Pattern(
        regexp = "^([가-힣]{1,20}|[a-zA-Z]{1,20})$",
        message = "이름은 한글 또는 영어만 사용할 수 있으며, 1~20자 이내여야 합니다.",
    )
    @field:NotBlank(message = "인물 이름은 필수입니다.")
    val figureName: String,
    @field:NotBlank(message = "카테고리는 필수입니다.")
    val categoryId: String,
    @field:NotBlank(message = "이미지 URL은 필수입니다.")
    val imageUrl: String,
    @field:NotBlank(message = "소개글은 필수입니다.")
    val bio: String,
) {
    fun toData(user: User): CreateFiureData =
        CreateFiureData(
            user = user,
            figureName = figureName,
            categoryId = categoryId,
            imageUrl = imageUrl,
            bio = bio,
        )
}
