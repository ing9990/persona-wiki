package io.ing9990.domain.activities.events.handler

import io.ing9990.domain.activities.events.CreateActivityEvent
import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.CommentInteraction
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.vote.Vote
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ActivityEventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun publishCommentCreated(comment: Comment) {
        val event = CreateActivityEvent.createCommentEvent(comment, this)
        eventPublisher.publishEvent(event)
    }

    fun publishReplyCreated(reply: Comment) {
        val event = CreateActivityEvent.createReplyEvent(reply, this)
        eventPublisher.publishEvent(event)
    }

    fun publishFigureAdded(
        figure: Figure,
        userId: Long,
    ) {
        val event = CreateActivityEvent.createFigureAddedEvent(figure, userId, this)
        eventPublisher.publishEvent(event)
    }

    fun publishVoteCreated(vote: Vote) {
        val event = CreateActivityEvent.createVoteEvent(vote, this)
        eventPublisher.publishEvent(event)
    }

    fun publishCommentLike(interaction: CommentInteraction) {
        val event = CreateActivityEvent.createComentLikeEvent(interaction, this)
        eventPublisher.publishEvent(event)
    }

    fun publishCommentLiked(interaction: CommentInteraction) {
        val event = CreateActivityEvent.createCommentLikedEvent(interaction, this)
        eventPublisher.publishEvent(event)
    }

    fun publishCommentDislike(interaction: CommentInteraction) {
        val event = CreateActivityEvent.createCommentDislikeEvent(interaction, this)
        eventPublisher.publishEvent(event)
    }
}
