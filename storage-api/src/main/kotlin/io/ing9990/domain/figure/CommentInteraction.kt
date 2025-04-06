package io.ing9990.domain.figure

import io.ing9990.domain.user.User
import io.ing9990.model.BaseEntity
import jakarta.persistence.*

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
