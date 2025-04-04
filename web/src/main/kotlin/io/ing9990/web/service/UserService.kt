package io.ing9990.web.service

import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.domain.EntityNotFoundException
import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.User
import io.ing9990.domain.user.repositories.UserRepository
import io.ing9990.web.exceptions.UnauthorizedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = true)
    fun findUserById(id: Long): User? = userRepository.findUserById(id)

    @Transactional(readOnly = true)
    fun getUserById(userId: Long): User = findUserById(userId) ?: throw EntityNotFoundException("User", userId)

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
                image = profile.findImageUrl(),
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
        val providerPrefix =
            when (providerType) {
                OAuthProviderType.KAKAO -> "K"
                OAuthProviderType.NAVER -> "N"
                OAuthProviderType.GOOGLE -> "G"
            }

        return "${providerPrefix}${profile.findUsername()}${profile.findSocialId().takeLast(5)}"
    }

    @Transactional
    fun updateProfileImage(userId: Long?): User =
        userId?.let { id ->
            getUserById(id).apply { removeProfileImage() }
        } ?: throw UnauthorizedException()

    @Transactional
    fun updateNickname(
        userId: Long,
        nickname: String,
    ): User {
        val user = getUserById(userId)
        user.nickname = nickname
        return userRepository.save(user)
    }
}
