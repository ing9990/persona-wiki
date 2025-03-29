package io.ing9990.domain.figure

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Reputation(
    @Column(name = "like_count", columnDefinition = "INT DEFAULT 0")
    var likeCount: Int = 0,

    @Column(name = "dislike_count", columnDefinition = "INT DEFAULT 0")
    var dislikeCount: Int = 0,

    @Column(name = "neutral_count", columnDefinition = "INT DEFAULT 0")
    var neutralCount: Int = 0,
) {
    fun total(): Int = likeCount + dislikeCount + neutralCount

    fun likeRatio(): Double = if (total() > 0) likeCount.toDouble() / total() else 0.0

    fun dislikeRatio(): Double = if (total() > 0) dislikeCount.toDouble() / total() else 0.0

    fun neutralRatio(): Double = if (total() > 0) neutralCount.toDouble() / total() else 0.0
}