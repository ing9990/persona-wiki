package io.ing9990.api.figures.dto.response

/**
 * 인물 평판 응답을 위한 DTO
 */
data class ReputationResponse(
    val likeCount: Int,
    val dislikeCount: Int,
    val neutralCount: Int,
    val likeRatio: Double,
    val dislikeRatio: Double,
    val neutralRatio: Double,
    val total: Int,
)
