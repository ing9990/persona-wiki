package io.ing9990.aop.resolver.dto

data class RankingResultWithRank(
    val userId: Long,
    val nickname: String,
    val bio: String,
    val prestige: Int,
    val rank: Int,
    val profileImage: String?,
)
