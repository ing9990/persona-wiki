package io.ing9990.domain.figure.service.dto

import io.ing9990.domain.user.User

class CreateFiureData(
    val user: User,
    val figureName: String,
    val categoryId: String,
    val imageUrl: String,
    val bio: String,
)
