// storage-api/src/main/kotlin/io/ing9990/domain/figure/InteractionType.kt
package io.ing9990.domain.comment

import io.ing9990.domain.activities.ActivityType

/**
 * 댓글에 대한 상호작용 유형을 나타내는 Enum 클래스
 * LIKE: 좋아요
 * DISLIKE: 싫어요
 */
enum class
InteractionType {
    LIKE,
    DISLIKE,
    ;

    fun InteractionType.toActivityType(): ActivityType =
        when (this) {
            LIKE -> ActivityType.LIKE
            DISLIKE -> ActivityType.DISLIKE
        }
}
