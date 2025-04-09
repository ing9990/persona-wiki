package io.ing9990.domain.vote

import io.ing9990.domain.figure.Figure
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
    name = "votes",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_vote_user_figure",
            columnNames = ["user_id", "figure_id"],
        ),
    ],
)
class Vote(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "figure_id", nullable = false)
    val figure: Figure,
    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment", nullable = false)
    var sentiment: Sentiment,
) : BaseEntity()
