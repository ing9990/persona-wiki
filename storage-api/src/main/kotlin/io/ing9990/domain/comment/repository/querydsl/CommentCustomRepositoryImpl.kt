package io.ing9990.domain.comment.repository.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.CommentType
import io.ing9990.domain.comment.querydsl.QComment
import io.ing9990.domain.comment.querydsl.QCommentInteraction
import io.ing9990.domain.user.querydsl.QUser
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class CommentCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CommentCustomRepository {
    companion object {
        private val log = LoggerFactory.getLogger(CommentCustomRepositoryImpl::class.java)
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

    override fun findCommentsWithUserInteractions(
        figureId: Long,
        userId: Long?,
        pageable: Pageable,
    ): Page<Comment> {
        val comment = QComment.comment
        val user = QUser.user
        val interaction = QCommentInteraction.commentInteraction

        // 기본 쿼리 구성
        val query =
            jpaQueryFactory
                .selectFrom(comment)
                .leftJoin(comment.user, user)
                .fetchJoin()

        // 로그인한 사용자의 상호작용 정보 가져오기
        if (userId != null) {
            query
                .leftJoin(comment.interactions, interaction)
                .on(interaction.user.id.eq(userId))
                .fetchJoin()
        }

        // 쿼리 실행 및 결과 가져오기
        val content =
            query
                .where(
                    comment.figure.id
                        .eq(figureId)
                        .and(comment.commentType.eq(CommentType.ROOT)),
                ).offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .orderBy(comment.createdAt.desc())
                .fetch()

        // 총 개수 쿼리
        val count =
            jpaQueryFactory
                .select(comment.countDistinct())
                .from(comment)
                .where(
                    comment.figure.id
                        .eq(figureId)
                        .and(comment.commentType.eq(CommentType.ROOT)),
                ).fetchOne() ?: 0L

        return PageImpl(content, pageable, count)
    }

    override fun findRepliesWithUserInteractions(
        parentId: Long,
        userId: Long?,
    ): List<Comment> {
        val comment = QComment.comment
        val user = QUser.user
        val interaction = QCommentInteraction.commentInteraction

        // 기본 쿼리 구성
        val query =
            jpaQueryFactory
                .selectFrom(comment)
                .leftJoin(comment.user, user)
                .fetchJoin()

        // 로그인한 사용자의 상호작용 정보 가져오기
        if (userId != null) {
            query
                .leftJoin(comment.interactions, interaction)
                .on(interaction.user.id.eq(userId))
                .fetchJoin()
        }

        return query
            .where(
                comment.parent.id
                    .eq(parentId)
                    .and(comment.commentType.eq(CommentType.REPLY)),
            ).orderBy(comment.createdAt.asc())
            .fetch()
    }
}
