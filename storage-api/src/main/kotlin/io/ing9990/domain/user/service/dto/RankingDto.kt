package io.ing9990.domain.user.service.dto

import io.ing9990.domain.user.User

/**
 * 랭킹 페이지에 필요한 모든 데이터를 담는 결과 클래스
 */
data class RankingPageResult(
    val rankings: List<UserRankingInfo>,
    val totalUsers: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val topUsers: List<UserRankingInfo>,
)

/**
 * 개별 사용자의 랭킹 정보
 */
data class UserRankingInfo(
    val userId: Long,
    val nickname: String,
    val bio: String,
    val prestige: Int,
    val rank: Int,
    val profileImage: String?,
) {
    companion object {
        fun from(
            user: User,
            rank: Int,
        ): UserRankingInfo =
            UserRankingInfo(
                userId = user.id!!,
                nickname = user.nickname,
                bio = user.bio,
                prestige = user.prestige,
                rank = rank,
                profileImage = user.image,
            )
    }
}
