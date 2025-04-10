package io.ing9990.domain.user.repositories

import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.User
import io.ing9990.domain.user.repositories.querydsl.UserCustomRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository :
    JpaRepository<User, Long>,
    UserCustomRepository {
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

    @Query("select u from User u where u.id = ?1")
    fun findUserById(id: Long): User?

    /**
     *  내가 아닌 사용자들 중에서 파라미터의 nickname과 동일한 nickname을 가진 사용자가 있는지 확인합니다.
     *
     * @return
     *   중복된 닉네임이 존재하지 않으면 true를 반환하고,
     *   존재하면 false를 반환합니다.
     */
    @Query(
        """
    SELECT CASE WHEN COUNT(u) = 0 THEN false ELSE true END
    FROM User u
    WHERE u.nickname = :nickname
      AND u.id <> :#{#user.id}
    """,
    )
    fun hasDuplicatedNickname(
        @Param("user") user: User,
        @Param("nickname") nickname: String,
    ): Boolean
}
