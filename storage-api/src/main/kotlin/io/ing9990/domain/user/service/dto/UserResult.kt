package io.ing9990.domain.user.service.dto

import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.User
import java.time.LocalDateTime

data class UserResult(
    val userId: Long,
    val nickname: String,
    val image: String,
    val provider: OAuthProviderType,
    val lastLoginAt: LocalDateTime,
) {
    fun from(user: User): UserResult =
        UserResult(
            userId = user.id!!,
            nickname = user.nickname,
            image = user.image,
            provider = user.provider!!,
            lastLoginAt = user.lastLoginAt,
        )
}
