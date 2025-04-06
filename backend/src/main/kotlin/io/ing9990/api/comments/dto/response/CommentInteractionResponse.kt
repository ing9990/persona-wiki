package io.ing9990.api.comments.dto.response

import io.ing9990.domain.figure.InteractionType

data class CommentInteractionResponse(
    val commentId: Long,
    val interactionType: InteractionType?,
    val isInteracted: Boolean,
) {
    companion object {
        fun from(
            commentId: Long,
            interactionType: InteractionType?,
        ): CommentInteractionResponse =
            CommentInteractionResponse(
                commentId = commentId,
                interactionType = interactionType,
                isInteracted = interactionType != null,
            )
    }
}
