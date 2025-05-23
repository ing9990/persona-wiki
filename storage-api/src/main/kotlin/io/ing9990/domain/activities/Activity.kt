package io.ing9990.domain.activities

import io.ing9990.domain.activities.ActivityType.COMMENT
import io.ing9990.domain.activities.ActivityType.DISLIKE
import io.ing9990.domain.activities.ActivityType.DISLIKED
import io.ing9990.domain.activities.ActivityType.LIKE
import io.ing9990.domain.activities.ActivityType.LIKED
import io.ing9990.domain.activities.ActivityType.REPLY
import io.ing9990.domain.user.User
import io.ing9990.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "activities")
class Activity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val activityType: ActivityType,
    @Column(nullable = false)
    val targetId: Long,
    @Column(nullable = false)
    val targetName: String,
    @Column(nullable = false)
    val prestigeAmount: Int,
    @Column(length = 500)
    val description: String? = null,
    @Column(name = "category_id", nullable = false)
    val categoryId: String,
    @Column(name = "comment_id", nullable = true)
    val commentId: Long? = null,
) : BaseEntity() {
    fun isCommentActivity(): Boolean =
        listOf(
            COMMENT,
            REPLY,
            LIKE,
            DISLIKE,
            DISLIKED,
            LIKED,
        ).contains(activityType)
}
