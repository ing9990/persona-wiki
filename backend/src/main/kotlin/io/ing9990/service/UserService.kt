package io.ing9990.service

import io.ing9990.authentication.OAuthUserProfile
import io.ing9990.domain.EntityNotFoundException
import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.User
import io.ing9990.domain.user.User.Companion.regex
import io.ing9990.domain.user.repositories.UserRepository
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

    @Transactional(readOnly = true)
    fun findByNickname(nickname: String): User? = userRepository.findByNickname(nickname)

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
            User.create(
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
    fun removeProfileImage(userId: Long): User = getUserById(userId).apply { removeProfileImage() }

    @Transactional
    fun updateNickname(
        userId: Long,
        nickname: String,
    ): User {
        val user = getUserById(userId)
        updateNickNameValidation(user, nickname)
        user.nickname = nickname
        return user
    }

    @Transactional
    fun updateBio(
        userId: Long,
        bio: String,
    ): User =
        getUserById(userId)
            .let {
                it.updateBio(bio)
                it
            }

    private fun updateNickNameValidation(
        user: User,
        nickname: String,
    ) {
        // 닉네임이 공백일 수 없음 (비어있으면 안 됨)
        check(nickname.isNotBlank()) {
            "닉네임을 비워둘 수 없습니다."
        }

        // 닉네임 길이가 2자리 ~ 12자리 인지 검증
        check(nickname.length in 2..12) {
            "닉네임은 2자리 ~ 12자리 사이여야 합니다."
        }

        // 닉네임이 영문, 숫자, 한글만 포함하는지 검증
        check(nickname.matches(regex)) {
            "닉네임은 영문, 숫자, 한글만 사용할 수 있습니다."
        }

        // 자기 자신을 제외하고 동일한 닉네임이 있는지 확인
        check(!userRepository.hasDuplicatedNickname(user, nickname)) {
            "중복된 닉네임입니다: [$nickname]"
        }
    }
}
