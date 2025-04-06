package io.ing9990.domain.vote.service

import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.repository.FigureRepository
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.vote.Vote
import io.ing9990.domain.vote.repository.VoteRepository
import io.ing9990.domain.vote.service.dto.VoteData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VoteService(
    private val figureService: FigureService,
    private val figureRepository: FigureRepository,
    private val categoryService: CategoryService,
    private val voteRepository: VoteRepository,
) {
    /**
     * 투표
     */
    @Transactional
    fun voteFigure(voteData: VoteData): Figure {
        validateVotable(voteData)

        val figure: Figure =
            figureService.findByCategoryIdAndNameWithDetails(
                voteData.categoryId,
                voteData.figureName,
            )

        val voteCreated =
            Vote(
                user = voteData.user,
                figure = figure,
                sentiment = voteData.sentiment,
            )

        val voteSaved = voteRepository.save(voteCreated)
        figure.addVote(voteSaved)
        return figureRepository.save(figure)
    }

    /**
     * 투표한 적이 있는지 검증하면 된다.
     */
    private fun validateVotable(voteData: VoteData) {
        TODO("Not yet implemented")
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
