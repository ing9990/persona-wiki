package io.ing9990.domain.figure.repository.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.figure.Comment
import io.ing9990.domain.figure.querydsl.QComment
import org.springframework.stereotype.Repository

@Repository
class CommentQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) : CommentCustomRepository {
    override fun findWithRepliesById(commentId: Long): Comment? {
        val comment = QComment.comment
        val reply = QComment.comment

        return queryFactory
            .selectDistinct(comment)
            .from(comment)
            .leftJoin(comment.replies, reply).fetchJoin()
            .where(comment.id.eq(commentId))
            .fetchOne()
    }
}
