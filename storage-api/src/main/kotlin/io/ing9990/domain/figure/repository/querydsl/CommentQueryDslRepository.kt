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

        return queryFactory
            .selectFrom(comment)
            .leftJoin(comment.replies).fetchJoin()
            .where(comment.id.eq(commentId))
            .fetchOne()
    }
}
