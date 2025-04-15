package io.ing9990.domain.user.repositories.querydsl

import io.ing9990.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 랭킹 관련 JPA 리포지토리
 */
@Repository
interface RankingRepository :
    JpaRepository<User, Long>,
    RankingCustomRepository {
    /**
     * 특정 사용자의 랭킹 순위를 조회합니다.
     */
    @Query(
        """
        SELECT COUNT(*) + 1 FROM User u 
        WHERE u.prestige > (SELECT u2.prestige FROM User u2 WHERE u2.id = :userId)
    """,
    )
    fun getUserRank(
        @Param("userId") userId: Long,
    ): Int

    /**
     * 전체 사용자 수를 조회합니다.
     */
    @Query("SELECT COUNT(u) FROM User u")
    fun countAllUsers(): Long
}
