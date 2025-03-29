package io.ing9990.domain.figure

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
import java.util.*

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
    val comments: MutableList<Comment> = mutableListOf(),
) : BaseEntity() {
    /**
     * 댓글을 추가합니다.
     */
    fun addComment(comment: Comment) {
        comments.add(comment)
    }

    /**
     * URL 경로로 사용할 수 있는 형태의 이름을 반환합니다.
     */
    fun getUrlName(): String {
        return name.replace(" ", "-").lowercase(Locale.getDefault())
    }
}
