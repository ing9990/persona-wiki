package io.ing9990.domain.vote.service

import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.Sentiment
import io.ing9990.domain.figure.repository.FigureRepository
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VoteService(
    private val figureService: FigureService,
    private val figureRepository: FigureRepository,
) {
    /**
     * 투표
     */
    @Transactional
    fun voteFigure(
        figureId: Long,
        sentiment: Sentiment,
        user: User,
    ): Figure {
        val figure = figureService.findById(figureId)

        figure.addOrUpdateVote(user, sentiment)

        return figureRepository.save(figure)
    }

    /**
     * 사용자가 특정 인물에 대해 이미 투표했는지 확인합니다.
     */
    fun hasUserVoted(
        figureId: Long,
        userId: Long?,
    ): Boolean {
        val figure = figureService.findById(figureId)
        return userId?.let { id -> figure.hasVoted(id) } ?: false
    }
}
