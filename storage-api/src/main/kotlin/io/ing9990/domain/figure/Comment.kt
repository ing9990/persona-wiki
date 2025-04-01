// storage-api/src/main/kotlin/io/ing9990/domain/figure/Comment.kt
package io.ing9990.domain.figure

import io.ing9990.domain.figure.CommentType.ROOT
import io.ing9990.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "figure_id")
    val figure: Figure,
    @Column(name = "content", nullable = false)
    var content: String,
    @Column(name = "likes")
    var likes: Int = 0,
    @Column(name = "dislikes")
    var dislikes: Int = 0,
    // 부모 댓글 참조 (자기 참조 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    val parent: Comment? = null,
    // 댓글 깊이 (0: 원 댓글, 1: 답글, ...)
    @Column(name = "depth")
    val depth: Int = 0,
    // 원 댓글 ID (최상위 부모 댓글의 ID)
    @Column(name = "root_id")
    val rootId: Long? = null,
    @Column(name = "replies_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    var repliesCount: Int = 0,
    // 댓글 유형 (ROOT 또는 REPLY)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ROOT'")
    @Column(name = "comment_type", nullable = false)
    val commentType: CommentType = ROOT,
    // 자식 댓글 목록 (답글들)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    val replies: MutableList<Comment> = mutableListOf(),
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
}
