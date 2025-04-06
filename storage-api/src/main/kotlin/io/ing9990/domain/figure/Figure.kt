package io.ing9990.domain.figure

import io.ing9990.domain.category.Category
import io.ing9990.domain.comment.Comment
import io.ing9990.domain.vote.Vote
import io.ing9990.model.BaseEntity
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "figure_id") val id: Long? = null,
    @Column(name = "name", nullable = false) var name: String,
    @Column(name = "image_url", nullable = false) var imageUrl: String,
    @Column(name = "biography") var bio: String? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(
        name = "category_id",
        nullable = false,
    ) var category: Category,
    @OneToMany(
        mappedBy = "figure",
        cascade = [ALL],
        orphanRemoval = true,
    ) val votes: MutableSet<Vote> = mutableSetOf(),
    @OneToMany(
        mappedBy = "figure",
        cascade = [ALL],
        orphanRemoval = true,
    ) val comments: MutableList<Comment> = mutableListOf(),
) : BaseEntity() {
    /**
     * Vote를 추가합니다.
     */
    fun addVote(savedVote: Vote) {
        votes.add(savedVote)
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
