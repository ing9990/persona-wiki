package io.ing9990.domain.figure

import io.ing9990.domain.user.User
import io.ing9990.model.BaseEntity
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.Locale

@Table(
    name = "figure",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_figure_name_category",
            columnNames = ["name", "category_id"],
        ),
    ],
)
@Entity(name = "figure")
class Figure(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "figure_id")
    val id: Long? = null,
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "image_url")
    var imageUrl: String? = null,
    @Column(name = "biography")
    var bio: String? = null,
    @Embedded
    val reputation: Reputation = Reputation(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,
    @OneToMany(mappedBy = "figure", cascade = [ALL], orphanRemoval = true)
    val votes: MutableList<Vote> = mutableListOf(),
    @OneToMany(mappedBy = "figure", cascade = [ALL], orphanRemoval = true)
    val comments: MutableList<Comment> = mutableListOf(),
) : BaseEntity() {
    /**
     * 사용자의 투표를 추가하거나 업데이트합니다.
     */
    fun addOrUpdateVote(
        user: User,
        sentiment: Sentiment,
    ): Vote {
        // 기존 투표가 있는지 확인
        val existingVote = votes.find { it.user.id == user.id }

        return if (existingVote != null) {
            // 기존 투표 업데이트
            existingVote.sentiment = sentiment
            existingVote
        } else {
            // 새 투표 추가
            val vote = Vote(user = user, figure = this, sentiment = sentiment)
            votes.add(vote)

            // Reputation 업데이트
            when (sentiment) {
                Sentiment.POSITIVE -> reputation.likeCount++
                Sentiment.NEUTRAL -> reputation.neutralCount++
                Sentiment.NEGATIVE -> reputation.dislikeCount++
            }

            vote
        }
    }

    /**
     * 특정 사용자가 이미 투표했는지 확인합니다.
     */
    fun hasVoted(userId: Long): Boolean = votes.any { it.user.id == userId }

    /**
     * 특정 사용자의 투표 정보를 가져옵니다.
     */
    fun getUserVote(userId: Long): Vote? = votes.find { it.user.id == userId }

    /**
     * URL 경로로 사용할 수 있는 형태의 이름을 반환합니다.
     */
    fun getUrlName(): String = name.replace(" ", "-").lowercase(Locale.getDefault())
}
