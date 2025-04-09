package io.ing9990.domain.vote.service.dto

import io.ing9990.domain.user.User
import io.ing9990.domain.vote.Sentiment

class VoteData(
    val user: User,
    val categoryId: String,
    val figureName: String,
    val sentiment: Sentiment,
)
