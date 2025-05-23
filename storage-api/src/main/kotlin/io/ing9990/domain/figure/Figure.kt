package io.ing9990.domain.figure

import io.ing9990.common.To
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
class Figure protected constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "figure_id") val id: Long? = null,
    @Column(name = "name", nullable = false, length = 20) var name: String,
    @Column(name = "image_url", nullable = false, length = 1200) var imageUrl: String,
    @Column(name = "biography", nullable = true, length = 1200) var bio: String,
    @Column(name = "chosung") val chosung: String,
    @Column(name = "slug", nullable = false, unique = true, length = 50)
    val slug: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
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
    companion object {
        fun create(
            name: String,
            imageUrl: String,
            bio: String,
            category: Category,
        ): Figure {
            val chosung = getChosungFrom(name)
            return Figure(
                name = name,
                imageUrl = imageUrl,
                bio = bio,
                category = category,
                chosung = chosung,
                slug = To.slug(name),
            )
        }

        private fun getChosungFrom(name: String): String =
            name
                .filter { it.isLetter() }
                .map {
                    when (it) {
                        in '가'..'힣' -> ((it.code - 0xAC00) / 28 / 21 + 0x1100).toChar()
                        in 'A'..'Z', in 'a'..'z' -> it.uppercaseChar()
                        else -> null
                    }
                }.joinToString("")
    }

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
}
