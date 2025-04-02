package io.ing9990.domain.user.repositories

import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    /**
     * 프로바이더 ID와 프로바이더 타입으로 사용자를 찾습니다.
     * @param providerId 소셜 로그인 제공자 ID
     * @param providerType 소셜 로그인 제공자 타입
     * @return 사용자 엔티티 또는 null
     */
    @Query("select u from User u where u.providerId = ?1 and u.provider = ?2")
    fun findByProviderIdAndProvider(
        providerId: String,
        providerType: OAuthProviderType,
    ): User?

    /**
     * 닉네임으로 사용자를 찾습니다.
     * @param nickname 닉네임
     * @return 사용자 엔티티 또는 null
     */
    @Query("select u from User u where u.nickname = ?1")
    fun findByNickname(nickname: String): User?
}
