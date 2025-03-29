package io.ing9990.api.comments.dto.request

import io.ing9990.domain.figure.Sentiment

/**
 * 댓글 요청을 위한 DTO
 */
data class CommentRequest(
    val content: String,
    val sentiment: Sentiment,
)
