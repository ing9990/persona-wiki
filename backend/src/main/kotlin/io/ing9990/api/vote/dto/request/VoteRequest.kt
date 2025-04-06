package io.ing9990.api.vote.dto.request

import io.ing9990.domain.vote.Sentiment
import org.jetbrains.annotations.NotNull

data class VoteRequest(
    @NotNull
    val sentiment: Sentiment,
)
