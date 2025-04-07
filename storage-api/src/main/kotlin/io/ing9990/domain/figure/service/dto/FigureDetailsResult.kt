package io.ing9990.domain.figure.service.dto

import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import io.ing9990.domain.vote.Sentiment
import io.ing9990.domain.vote.Sentiment.NEGATIVE
import io.ing9990.domain.vote.Sentiment.NEUTRAL
import io.ing9990.domain.vote.Sentiment.POSITIVE
import io.ing9990.domain.vote.Vote
import java.time.LocalDateTime

data class FigureDetailsResult(
    // Figures
    val id: Long,
    val name: String,
    val imageUrl: String,
    val bio: String?,
    // Votes
    val userVote: Vote?,
    val sentimentCounts: Map<Sentiment, Int>,
    // pagination
    val totalPages: Int,
    val totalCommentCount: Long,
    val comments: List<CommentResult>,
    val commentsSize: Long,
    // categories
    val categoryId: String,
    val categoryDisplayName: String,
    val categoryImage: String,
    val categoryDescription: String,
    // Base
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    fun hasSentiment(): Boolean = userVote != null

    fun positiveCount(): Int = sentimentCounts[POSITIVE] ?: 0

    fun negativeCount(): Int = sentimentCounts[Sentiment.NEGATIVE] ?: 0

    fun neutralCount(): Int = sentimentCounts[Sentiment.NEUTRAL] ?: 0

    fun totalVotes(): Long = sentimentCounts.values.sum().toLong()

    fun isCommentEmpty(): Boolean = totalCommentCount == 0L

    fun percentageString(count: Int): String =
        if (totalVotes() == 0L) {
            "0 (0%)"
        } else {
            val percent = (count.toDouble() / totalVotes()) * 100.0
            "$count (${String.format("%.1f", percent)}%)"
        }

    fun positivePercentage(): String = percentageString(positiveCount())

    fun neutralPercentage(): String = percentageString(neutralCount())

    fun negativePercentage(): String = percentageString(negativeCount())

    fun isPositive(): Boolean = userVote?.sentiment == POSITIVE

    fun isNegative(): Boolean = userVote?.sentiment == NEGATIVE

    fun isNeutral(): Boolean = userVote?.sentiment == NEUTRAL
}
