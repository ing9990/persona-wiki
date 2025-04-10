package io.ing9990.domain.activities.repositories.querydsl.dto

data class ActivityOverviewResult(
    val commentCount: Int,
    val voteCount: Int,
    val personAddCount: Int,
) {
    companion object {
        fun of(
            commentCount: Int,
            voteCount: Int,
            personAddCount: Int,
        ): ActivityOverviewResult =
            ActivityOverviewResult(
                commentCount = commentCount,
                voteCount = voteCount,
                personAddCount = personAddCount,
            )
    }
}
