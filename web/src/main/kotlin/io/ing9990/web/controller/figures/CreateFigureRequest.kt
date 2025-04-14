package io.ing9990.web.controller.figures

import com.fasterxml.jackson.annotation.JsonProperty
import io.ing9990.domain.figure.service.dto.CreateFiureData
import io.ing9990.domain.user.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateFigureRequest(
    @Size(min = 2, max = 20)
    @field:NotBlank(message = "인물 이름은 필수입니다.")
    @JsonProperty("figureName")
    val figureName: String,
    @field:NotBlank(message = "카테고리는 필수입니다.")
    @JsonProperty("categoryId")
    val categoryId: String,
    @field:NotBlank(message = "이미지 URL은 필수입니다.")
    @JsonProperty("imageUrl")
    val imageUrl: String,
    @field:NotBlank(message = "소개글은 필수입니다.")
    @JsonProperty("bio")
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
