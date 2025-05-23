package io.ing9990.domain.fixtures

import io.ing9990.domain.user.OAuthProviderType.KAKAO
import io.ing9990.domain.user.User

enum class UserFixtures(
    val user: User,
) {
    USER_1(
        User(
            id = 1L,
            providerId = "098721",
            bio = "테스트 유저입니다.",
            provider = KAKAO,
            nickname = "테스트 유저",
            image = "https://test-image/test.png",
        ),
    ),
}
