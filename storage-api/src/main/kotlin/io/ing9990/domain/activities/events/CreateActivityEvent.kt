package io.ing9990.domain.activities.events

import io.ing9990.domain.activities.ActivityEvent
import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.CommentInteraction
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.vote.Vote
import org.springframework.context.ApplicationEvent

class CreateActivityEvent(
    source: Any,
    val activityEvent: ActivityEvent,
) : ApplicationEvent(source) {
    companion object {
        // 팩토리 메서드들
        fun createCommentEvent(
            comment: Comment,
            source: Any,
        ): CreateActivityEvent = CreateActivityEvent(source, CommentCreatedEvent.from(comment))

        fun createReplyEvent(
            reply: Comment,
            source: Any,
        ): CreateActivityEvent = CreateActivityEvent(source, ReplyCreatedEvent.from(reply))

        fun createFigureAddedEvent(
            figure: Figure,
            userId: Long,
            source: Any,
        ): CreateActivityEvent = CreateActivityEvent(source, FigureAddedEvent.from(figure, userId))

        fun createVoteEvent(
            vote: Vote,
            source: Any,
        ): CreateActivityEvent = CreateActivityEvent(source, VoteCreatedEvent.from(vote))

        fun createComentLikeEvent(
            interaction: CommentInteraction,
            source: Any,
        ): CreateActivityEvent = CreateActivityEvent(source, CommentLikeEvent.from(interaction))

        fun createCommentLikedEvent(
            interaction: CommentInteraction,
            source: Any,
        ): CreateActivityEvent = CreateActivityEvent(source, CommentLikedEvent.from(interaction))

        fun createCommentDislikeEvent(
            interaction: CommentInteraction,
            source: Any,
        ): CreateActivityEvent = CreateActivityEvent(source, CommentDislikeEvent.from(interaction))
    }
}
