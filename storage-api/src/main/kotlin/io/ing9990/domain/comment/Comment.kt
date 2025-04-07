// storage-api/src/main/kotlin/io/ing9990/domain/figure/Comment.kt
package io.ing9990.domain.comment

import io.ing9990.domain.comment.CommentType.ROOT
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.user.User
import io.ing9990.model.BaseEntity
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert

@DynamicInsert
@Entity(name = "comment")
class Comment(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    val id: Long? = null,
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "figure_id")
    val figure: Figure,
    @Column(name = "content", nullable = false, length = 1000)
    var content: String,
    @Column(name = "likes")
    var likes: Int = 0,
    @Column(name = "dislikes")
    var dislikes: Int = 0,
    // 부모 댓글
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    val parent: Comment? = null,
    // 답글의 깊이
    @Column(name = "depth")
    val depth: Int = 0,
    // 답글인 경우 댓글의 ID
    @Column(name = "root_id")
    val rootId: Long? = null,
    // 답글 Lazyload 용으로 답글 갯수 컬럼
    @Column(name = "replies_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    var repliesCount: Int = 0,
    // 댓글 or 답글
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ROOT'")
    @Column(name = "comment_type", nullable = false)
    val commentType: CommentType = ROOT,
    // 답글 목록
    @OneToMany(mappedBy = "parent", fetch = LAZY, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    val replies: MutableList<Comment> = mutableListOf(),
    // 사용자
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "user_id")
    val user: User? = null,
    // 좋아요/싫어요 상호작용
    @OneToMany(mappedBy = "comment", cascade = [ALL], orphanRemoval = true)
    val interactions: MutableList<CommentInteraction> = mutableListOf(),
) : BaseEntity() {
    /**
     * 이 댓글이 답글인지 확인합니다.
     * @return 답글이면 true, 원 댓글이면 false
     */
    fun isReply(): Boolean = commentType == CommentType.REPLY

    /**
     * 이 댓글이 원 댓글인지 확인합니다.
     * @return 원 댓글이면 true, 답글이면 false
     */
    fun isRootComment(): Boolean = commentType == ROOT

    /**
     * 답글을 추가합니다.
     * @param reply 추가할 답글
     */
    fun addReply(reply: Comment) {
        repliesCount++
        replies.add(reply)
    }

    fun getUserInteraction(userId: Long): CommentInteraction? = interactions.find { it.user.id == userId }

    fun addInteraction(interaction: CommentInteraction) {
        interactions.add(interaction)

        when (interaction.interactionType) {
            InteractionType.LIKE -> likes++
            InteractionType.DISLIKE -> dislikes++
        }
    }

    fun deleteInteraction(
        userId: Long,
        type: InteractionType,
    ) {
        val interaction = getUserInteraction(userId) ?: return

        // 상호작용 유형에 따라 카운트 감소
        when (interaction.interactionType) {
            InteractionType.LIKE -> likes--
            InteractionType.DISLIKE -> dislikes--
        }

        // 상호작용 삭제
        interactions.removeIf { it.user.id == userId }
    }

    fun updateInteraction(
        userId: Long,
        newType: InteractionType,
    ): Boolean {
        val interaction = getUserInteraction(userId) ?: return false

        // 이전 상호작용 타입에 따라 카운트 감소
        when (interaction.interactionType) {
            InteractionType.LIKE -> likes--
            InteractionType.DISLIKE -> dislikes--
        }

        // 새 상호작용 타입으로 카운트 증가
        when (newType) {
            InteractionType.LIKE -> likes++
            InteractionType.DISLIKE -> dislikes++
        }

        interaction.interactionType = newType
        return true
    }
}
