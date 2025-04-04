package io.ing9990.domain.figure.repository.querydsl.comment

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.figure.Comment
import io.ing9990.domain.figure.CommentType
import io.ing9990.domain.figure.querydsl.QComment
import io.ing9990.domain.user.querydsl.QUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class CommentQueryDslRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CommentCustomRepository {
    companion object {
        private val log = LoggerFactory.getLogger(CommentQueryDslRepositoryImpl::class.java)
    }

    override fun findWithRepliesById(commentId: Long): Comment? {
        val comment = QComment.comment
        val reply = QComment.comment
        val user = QUser.user

        try {
            return jpaQueryFactory
                .selectDistinct(comment)
                .from(comment)
                .leftJoin(comment.replies, reply)
                .fetchJoin()
                .leftJoin(comment.user, user)
                .fetchJoin()
                .leftJoin(reply.user, user)
                .fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne()
        } catch (e: Exception) {
            log.error("findWithRepliesById 쿼리 실행 중 오류 발생: {}", e.message)
            throw e
        }
    }

    override fun findRepliesWithUserByParentId(parentId: Long): List<Comment> {
        val comment = QComment.comment
        val user = QUser.user

        try {
            return jpaQueryFactory
                .selectFrom(comment)
                .leftJoin(comment.user, user)
                .fetchJoin()
                .where(
                    comment.parent.id
                        .eq(parentId)
                        .and(comment.commentType.eq(CommentType.REPLY)),
                ).orderBy(comment.createdAt.asc())
                .fetch()
        } catch (e: Exception) {
            log.error("findRepliesWithUserByParentId 쿼리 실행 중 오류 발생: {}", e.message)
            throw e
        }
    }
}
