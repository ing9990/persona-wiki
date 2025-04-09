package io.ing9990.domain.vote.repository

import io.ing9990.domain.vote.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VoteRepository : JpaRepository<Vote, Long> {
    @Query("select v from Vote v where v.user.id = ?1 and v.figure.id = ?2")
    fun findByUserIdAndFigureId(
        userId: Long,
        figureId: Long,
    ): Vote?

    @Query("select count(v) > 0 from Vote v where v.user.id = ?1 and v.figure.id = ?2")
    fun existsByUserIdAndFigureId(
        userId: Long,
        figureId: Long,
    ): Boolean
}
