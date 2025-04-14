package io.ing9990.domain.vote.service.dto

import io.ing9990.domain.user.User
import io.ing9990.domain.vote.Sentiment

data class VoteData(
    val user: User,
    val categoryId: String,
    val slug: String,
    val sentiment: Sentiment,
)
