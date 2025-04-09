package io.ing9990.domain.figure.service.dto

import io.ing9990.domain.figure.Figure
import io.ing9990.domain.vote.Sentiment

class FigureCardResult(
    val categoryId: String,
    val categoryName: String,
    val name: String,
    val bio: String,
    val image: String,
    val positives: Long,
    val negatives: Long,
    val neutrals: Long,
) {
    companion object {
        fun from(figure: Figure): FigureCardResult =
            FigureCardResult(
                categoryId = figure.category.id,
                categoryName = figure.category.displayName,
                name = figure.name,
                bio = figure.bio ?: "소개가 등록되지 않았습니다.",
                image = figure.imageUrl,
                positives = figure.votes.count { it.sentiment == Sentiment.POSITIVE }.toLong(),
                negatives =
                    figure.votes
                        .count { it.sentiment == Sentiment.NEGATIVE }
                        .toLong(),
                neutrals = figure.votes.count { it.sentiment == Sentiment.NEUTRAL }.toLong(),
            )
    }
}
