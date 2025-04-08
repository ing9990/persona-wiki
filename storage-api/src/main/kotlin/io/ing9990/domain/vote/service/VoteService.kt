package io.ing9990.domain.vote.service

import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.repository.FigureRepository
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.user.User
import io.ing9990.domain.vote.Vote
import io.ing9990.domain.vote.repository.VoteRepository
import io.ing9990.domain.vote.service.dto.VoteData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VoteService(
    private val figureService: FigureService,
    private val figureRepository: FigureRepository,
    private val voteRepository: VoteRepository,
) {
    /**
     * 투표
     */
    @Transactional
    fun voteFigure(voteData: VoteData) {
        val figure: Figure =
            figureService.findFigureByCategoryIdAndName(
                voteData.categoryId,
                voteData.figureName,
            )

        validateVotable(figure, voteData.user)

        val voteCreated =
            Vote(
                user = voteData.user,
                figure = figure,
                sentiment = voteData.sentiment,
            )
        val voteSaved = voteRepository.save(voteCreated)
        figure.addVote(voteSaved)
        figureRepository.save(figure)
    }

    private fun validateVotable(
        figure: Figure,
        user: User,
    ) {
        require(!hasUserVoted(figure.id!!, user.id)) {
            "이미 베팅한 인물입니다. figure=$figure, user=$user"
        }
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

    /**
     * 사용자의 투표 정보를 가져옵니다.
     */
    fun getUserVote(
        figureId: Long,
        userId: Long?,
    ): Vote? {
        val figure = figureService.findById(figureId)
        return userId?.let { id -> figure.getUserVote(id) }
    }
}
