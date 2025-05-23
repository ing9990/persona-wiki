package io.ing9990.web.controller.users

data class UpdateBioRequest(
    val bio: String,
)

data class UpdateBioResponse(
    val success: Boolean,
    val message: String,
)
