package io.ing9990.domain.comment

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
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "comment_interactions",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_comment_interaction_user_comment",
            columnNames = ["user_id", "comment_id"],
        ),
    ],
)
class CommentInteraction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interaction_id")
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    val comment: Comment,
    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    var interactionType: InteractionType,
) : BaseEntity()
