package io.ing9990.web.service

import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.User
import io.ing9990.domain.user.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(private val userRepository: UserRepository) {
    /**
     * 소셜 로그인 정보를 기반으로 사용자를 저장하거나 업데이트합니다.
     * @param profile 소셜 로그인 제공자로부터 받은 사용자 프로필
     * @param providerType 소셜 로그인 제공자 타입
     * @return 저장 또는 업데이트된 사용자 정보
     */
    @Transactional
    fun saveOrUpdateUser(
        profile: OAuthUserProfile,
        providerType: OAuthProviderType,
    ): User {
        val socialId = profile.findSocialId()
        val existingUser =
            userRepository.findByProviderIdAndProvider(
                providerId = socialId,
                providerType = providerType,
            )

        return existingUser?.apply {
            this.lastLoginAt = LocalDateTime.now()
        } ?: userRepository.save(
            User(
                providerId = socialId,
                provider = providerType,
                profileImage = profile.findImageUrl(),
                nickname = generateNickname(profile, providerType),
                lastLoginAt = LocalDateTime.now(),
            ),
        )
    }

    /**
     * 임시 닉네임을 생성합니다.
     * @param profile 소셜 로그인 프로필
     * @param providerType 소셜 로그인 제공자 타입
     * @return 생성된 닉네임
     */
    private fun generateNickname(
        profile: OAuthUserProfile,
        providerType: OAuthProviderType,
    ): String {
        // 익명 사용자 + 프로바이더 이름 + ID의 일부로 닉네임 생성
        val providerPrefix =
            when (providerType) {
                OAuthProviderType.KAKAO -> "K"
                OAuthProviderType.NAVER -> "N"
                OAuthProviderType.GOOGLE -> "G"
            }

        val s = "${providerPrefix}${profile.findUsername()}${profile.findSocialId().takeLast(5)}"

        return s
    }
}
