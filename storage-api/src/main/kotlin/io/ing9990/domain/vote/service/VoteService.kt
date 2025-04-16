package io.ing9990.domain.vote.service

import io.ing9990.domain.activities.events.handler.ActivityEventPublisher
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.vote.Vote
import io.ing9990.domain.vote.repository.VoteRepository
import io.ing9990.domain.vote.service.dto.VoteData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VoteService(
    private val figureService: FigureService,
    private val voteRepository: VoteRepository,
    private val activityEventPublisher: ActivityEventPublisher,
) {
    /**
     * 투표
     */
    @Transactional // 트랜잭션 1 시작
    fun voteFigure(voteData: VoteData) {
        val figure: Figure =
            figureService.searchByCategoryIdAndName(
                voteData.categoryId,
                voteData.slug,
            )

        val voteCreated =
            Vote(
                user = voteData.user,
                figure = figure,
                sentiment = voteData.sentiment,
            )

        val voteSaved = voteRepository.save(voteCreated)

        figure.addVote(voteSaved)

        activityEventPublisher.publishVoteCreated(voteSaved)
    }
}
