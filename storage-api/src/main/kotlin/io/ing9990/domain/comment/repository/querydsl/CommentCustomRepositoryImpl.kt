package io.ing9990.domain.comment.repository.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.comment.CommentType
import io.ing9990.domain.comment.CommentType.ROOT
import io.ing9990.domain.comment.InteractionType
import io.ing9990.domain.comment.querydsl.QComment
import io.ing9990.domain.comment.querydsl.QCommentInteraction
import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import io.ing9990.domain.user.querydsl.QUser
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class CommentCustomRepositoryImpl(
    private val query: JPAQueryFactory,
) : CommentCustomRepository {
    companion object {
        private val log = LoggerFactory.getLogger(CommentCustomRepositoryImpl::class.java)
    }

    override fun findCommentsWithUserInteractions(
        figureId: Long,
        userId: Long,
        pageable: Pageable,
    ): Page<CommentResult> {
        val comment = QComment.comment
        val user = QUser.user
        val interaction = QCommentInteraction.commentInteraction

        // 1. 페이징된 루트 댓글 조회
        val comments =
            query
                .selectFrom(comment)
                .leftJoin(comment.user, user)
                .fetchJoin()
                .where(
                    comment.figure.id.eq(figureId),
                    comment.commentType.eq(ROOT),
                ).orderBy(comment.createdAt.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        if (comments.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        // 2. 댓글 ID 목록 추출
        val commentIds = comments.mapNotNull { it.id }

        // 3. 사용자의 상호작용 정보 조회
        val userInteractions =
            query
                .select(interaction.comment.id, interaction.interactionType)
                .from(interaction)
                .where(
                    interaction.user.id.eq(userId),
                    interaction.comment.id.`in`(commentIds),
                ).fetch()
                .associate { tuple ->
                    val commentId = tuple.get(0, Long::class.java)!!
                    val interactionType = tuple.get(1, InteractionType::class.java)!!
                    commentId to interactionType
                }

        // 4. CommentResult 변환
        val results =
            comments.map { comment ->
                val userInteractionType = userInteractions[comment.id]
                CommentResult(
                    id = comment.id!!,
                    content = comment.content,
                    likes = comment.likes,
                    dislikes = comment.dislikes,
                    name = comment.user?.nickname,
                    image = comment.user?.image ?: "",
                    createdAt = comment.createdAt,
                    userName = comment.user?.nickname ?: "알 수 없음",
                    userId = comment.user?.id ?: -1,
                    replyCount = comment.repliesCount,
                    interactionType = userInteractionType,
                )
            }

        // 5. 총 댓글 수 조회를 위한 count 쿼리
        val countQuery =
            query
                .select(comment.count())
                .from(comment)
                .where(
                    comment.figure.id.eq(figureId),
                    comment.commentType.eq(ROOT),
                ).fetchOne() ?: 0L

        return PageImpl(results, pageable, countQuery)
    }

    override fun findRepliesWithUserInteractions(
        parentId: Long,
        userId: Long?,
    ): List<CommentResult> {
        val comment = QComment.comment
        val user = QUser.user
        val interaction = QCommentInteraction.commentInteraction

        // 1. 부모 댓글의 답글 조회
        val replies =
            query
                .selectFrom(comment)
                .leftJoin(comment.user, user)
                .fetchJoin()
                .where(
                    comment.parent.id.eq(parentId),
                    comment.commentType.eq(CommentType.REPLY),
                ).orderBy(comment.createdAt.asc())
                .fetch()

        if (replies.isEmpty() || userId == null) {
            return replies.map { reply ->
                CommentResult(
                    id = reply.id!!,
                    content = reply.content,
                    likes = reply.likes,
                    dislikes = reply.dislikes,
                    name = reply.user?.nickname,
                    image = reply.user?.image ?: "",
                    createdAt = reply.createdAt,
                    userName = reply.user?.nickname ?: "알 수 없음",
                    userId = reply.user?.id ?: -1,
                    replyCount = 0,
                    interactionType = null,
                    isLikedByUser = false,
                )
            }
        }

        // 2. 답글 ID 목록 추출
        val replyIds = replies.mapNotNull { it.id }

        // 3. 사용자의 상호작용 정보 조회
        val userInteractions =
            query
                .select(interaction.comment.id, interaction.interactionType)
                .from(interaction)
                .where(
                    interaction.user.id.eq(userId),
                    interaction.comment.id.`in`(replyIds),
                ).fetch()
                .associate { tuple ->
                    val commentId = tuple.get(0, Long::class.java)!!
                    val interactionType = tuple.get(1, InteractionType::class.java)!!
                    commentId to interactionType
                }

        // 4. CommentResult 변환
        return replies.map { reply ->
            val userInteractionType = userInteractions[reply.id]
            val isLikedByUser = if (userInteractionType == InteractionType.LIKE) true else false

            CommentResult(
                id = reply.id!!,
                content = reply.content,
                likes = reply.likes,
                dislikes = reply.dislikes,
                name = reply.user?.nickname,
                image = reply.user?.image ?: "",
                createdAt = reply.createdAt,
                userName = reply.user?.nickname ?: "알 수 없음",
                userId = reply.user?.id ?: -1,
                replyCount = 0,
                interactionType = userInteractionType,
                isLikedByUser = isLikedByUser,
            )
        }
    }

    /**
     * 댓글과 상호작용을 페이징하여 불러옵니다.
     */
    override fun findCommentsByFigureIdWithUserInteractions(
        figureId: Long,
        userId: Long?,
        pageable: Pageable,
    ): Page<CommentResult> {
        val comment = QComment.comment
        val user = QUser.user
        val interaction = QCommentInteraction.commentInteraction

        // 1. 페이징된 루트 댓글 조회 (사용자 정보 포함)
        val comments =
            query
                .selectFrom(comment)
                .leftJoin(comment.user, user)
                .fetchJoin()
                .where(
                    comment.figure.id.eq(figureId),
                    comment.commentType.eq(ROOT),
                ).offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .orderBy(comment.createdAt.desc())
                .fetch()

        // 댓글이 없는 경우 빈 페이지 반환
        if (comments.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        // 2. 조회된 댓글 ID 목록
        val commentIds = comments.mapNotNull { it.id }

        // 3. 사용자의 상호작용 정보 조회
        val userInteractions =
            if (userId != null && userId > 0 && commentIds.isNotEmpty()) {
                query
                    .select(interaction.comment.id, interaction.interactionType)
                    .from(interaction)
                    .where(
                        interaction.user.id.eq(userId),
                        interaction.comment.id.`in`(commentIds),
                    ).fetch()
                    .associate { tuple ->
                        val commentId = tuple.get(0, Long::class.java)!!
                        val interactionType = tuple.get(1, InteractionType::class.java)!!
                        commentId to interactionType
                    }
            } else {
                emptyMap()
            }

        // 4. CommentResult 변환
        val results =
            comments.map { comment ->
                val userInteractionType = userInteractions[comment.id]
                val isLikedByUser = userInteractionType == InteractionType.LIKE

                CommentResult.from(
                    comment = comment,
                    replyCount = comment.repliesCount,
                    userInteractionType = userInteractionType,
                    isLikedByUser = isLikedByUser,
                )
            }

        // 5. 총 댓글 수 조회
        val totalCount = countCommentsByFigureId(figureId)

        return PageImpl(results, pageable, totalCount)
    }

    override fun countCommentsByFigureId(figureId: Long): Long {
        val comment = QComment.comment

        return query
            .select(comment.count())
            .from(comment)
            .where(
                comment.figure.id.eq(figureId),
                comment.commentType.eq(ROOT),
            ).fetchOne() ?: 0L
    }
}
