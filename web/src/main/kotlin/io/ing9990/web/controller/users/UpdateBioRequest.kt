package io.ing9990.web.controller.users

import jakarta.validation.constraints.NotNull

data class UpdateBioRequest(
    @NotNull(message = "한줄 소개를 입력해주세요.")
    val bio: String,
)
