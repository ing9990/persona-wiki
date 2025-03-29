package io.ing9990.api.figures.dto.response

/**
 * 인물 평가 응답을 위한 DTO
 */
data class VoteResponse(
    val success: Boolean,
    val message: String,
    val likeCount: Int,
    val dislikeCount: Int,
    val neutralCount: Int
)