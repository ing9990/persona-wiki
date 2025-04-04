package io.ing9990.domain.figure.repository.querydsl.comment

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.figure.Comment
import io.ing9990.domain.figure.querydsl.QComment
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class CommentQueryDslRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CommentCustomRepository {
    override fun findWithRepliesById(commentId: Long): Comment? {
        val comment = QComment.comment
        val reply = QComment.comment

        try {
            return jpaQueryFactory
                .selectDistinct(comment)
                .from(comment)
                .leftJoin(comment.replies, reply)
                .fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne()
        } catch (e: Exception) {
            log.error("findWithRepliesById 쿼리 실행 중 오류 발생: {}", e.message)
            throw e
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CommentQueryDslRepositoryImpl::class.java)
    }
}
