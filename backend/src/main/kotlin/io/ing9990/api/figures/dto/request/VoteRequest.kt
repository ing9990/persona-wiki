package io.ing9990.api.figures.dto.request

import io.ing9990.domain.figure.Sentiment

/**
 * 인물 평가 요청을 위한 DTO
 */
data class VoteRequest(
    val sentiment: Sentiment
)
