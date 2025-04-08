package io.ing9990.domain.figure.repository.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import io.ing9990.domain.EntityNotFoundException
import io.ing9990.domain.category.querydsl.QCategory
import io.ing9990.domain.comment.CommentType
import io.ing9990.domain.comment.InteractionType
import io.ing9990.domain.comment.querydsl.QComment
import io.ing9990.domain.comment.querydsl.QCommentInteraction
import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.querydsl.QFigure
import io.ing9990.domain.figure.service.dto.FigureCardResult
import io.ing9990.domain.figure.service.dto.FigureDetailsResult
import io.ing9990.domain.figure.service.dto.FigureMicroResult
import io.ing9990.domain.figure.service.dto.FigureMicroResults
import io.ing9990.domain.vote.Sentiment
import io.ing9990.domain.vote.querydsl.QVote
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import kotlin.math.ceil

/**
 * FigureCustomRepository 인터페이스 구현체
 * QueryDSL을 사용하여 복잡한 쿼리 실행
 */
@Repository
class FigureRepositoryImpl(
    private val query: JPAQueryFactory,
) : FigureCustomRepository {
    companion object {
        private val log = LoggerFactory.getLogger(FigureRepositoryImpl::class.java)
    }

    override fun searchByName(name: String): FigureMicroResults {
        val figure = QFigure.figure
        val category = QCategory.category

        val result =
            query
                .select(figure)
                .from(figure)
                .leftJoin(figure.category, category)
                .fetchJoin()
                .where(
                    figure.name
                        .like("%$name%")
                        .or(
                            figure.chosung.like("%$name%"),
                        ),
                ).limit(5)
                .fetch()
                .map {
                    FigureMicroResult(
                        categoryName = it.category.displayName,
                        figureName = it.name,
                        figureImage = it.imageUrl,
                        categoryId = it.category.id,
                    )
                }

        return FigureMicroResults(data = result)
    }

    override fun findPopularFigues(): List<FigureCardResult> {
        val figure = QFigure.figure
        val vote = QVote.vote
        val comment = QComment.comment

        val fetch: List<Figure> =
            query
                .select(figure)
                .from(figure)
                .leftJoin(figure.votes, vote)
                .leftJoin(figure.comments, comment)
                .groupBy(figure)
                .orderBy(
                    vote.count().add(comment.count()).desc(),
                ).limit(5)
                .fetch()

        return fetch.map { FigureCardResult.from(it) }
    }

    override fun findAllWithCategoryPopularOrder(): List<Figure> {
        val figure = QFigure.figure
        val category = QCategory.category

        try {
            return query
                .select(figure)
                .leftJoin(figure.category, category)
                .fetchJoin()
                .orderBy(figure.votes.size().asc())
                .fetch()
        } catch (e: Exception) {
            log.error("findAllWithCategoryPopularOrder failed ", e)
            throw e
        }
    }

    override fun findByIdWithVotes(id: Long): Figure? {
        val figure = QFigure.figure
        val vote = QVote.vote

        try {
            return query
                .selectFrom(figure)
                .leftJoin(figure.votes, vote)
                .fetchJoin() // votes 컬렉션 함께 로드
                .leftJoin(figure.category)
                .fetchJoin() // category 함께 로드
                .where(figure.id.eq(id))
                .fetchOne()
        } catch (e: Exception) {
            log.error("findByIdWithVotes 쿼리 실행 중 오류 발생: {}", e.message)
            throw e
        }
    }

    override fun findByIdWithDetails(id: Long): Figure? {
        val figure = QFigure.figure
        val vote = QVote.vote

        try {
            return query
                .selectFrom(figure)
                .leftJoin(figure.category)
                .fetchJoin()
                .leftJoin(figure.votes, vote)
                .fetchJoin()
                .where(figure.id.eq(id))
                .fetchOne()
        } catch (e: Exception) {
            log.error("findByIdWithDetails 쿼리 실행 중 오류 발생: {}", e.message)
            throw e
        }
    }

    override fun findByCategoryIdAndNameWithDetails(
        categoryId: String,
        figureName: String,
        userId: Long,
        commentPage: Int,
        commentSize: Int,
    ): FigureDetailsResult {
        val figure = QFigure.figure
        val vote = QVote.vote
        val comment = QComment.comment
        val interaction = QCommentInteraction.commentInteraction

        // 1. Figure + Category만 fetchJoin
        val figureResult =
            query
                .selectFrom(figure)
                .leftJoin(figure.category)
                .fetchJoin()
                .where(
                    figure.category.id
                        .eq(categoryId)
                        .and(figure.name.eq(figureName)),
                ).fetchOne()
                ?: throw EntityNotFoundException(
                    "Figure",
                    "$categoryId/$figureName",
                    "해당 인물을 찾을 수 없습니다.",
                )

        // 2. Sentiment별 득표 수
        val sentimentCounts: Map<Sentiment, Int> =
            query
                .select(vote.sentiment, vote.count())
                .from(vote)
                .where(vote.figure.eq(figureResult))
                .groupBy(vote.sentiment)
                .fetch()
                .associate { tuple ->
                    val sentiment = tuple.get(vote.sentiment)!!
                    val count = tuple.get(vote.count())?.toInt() ?: 0
                    sentiment to count
                }

        // 3. 사용자 투표
        val userVote =
            if (userId > 0) {
                query
                    .select(vote)
                    .from(vote)
                    .where(
                        vote.figure.eq(figureResult),
                        vote.user.id.eq(userId),
                    ).fetchOne()
            } else {
                null
            }

        // 4. 루트 댓글 페이징
        val pagedRootComments =
            query
                .selectFrom(comment)
                .leftJoin(comment.user)
                .fetchJoin()
                .where(
                    comment.figure.eq(figureResult),
                    comment.commentType.eq(CommentType.ROOT),
                ).orderBy(comment.createdAt.desc())
                .offset(commentPage.toLong() * commentSize)
                .limit(commentSize.toLong())
                .fetch()

        // 5. 전체 루트 댓글 개수
        val totalRootComments =
            query
                .select(comment.count())
                .from(comment)
                .where(
                    comment.figure.eq(figureResult),
                    comment.commentType.eq(CommentType.ROOT),
                ).fetchOne() ?: 0L

        val totalPages = ceil(totalRootComments.toDouble() / commentSize).toInt()

        // 6. 사용자가 상호작용한 댓글 정보 조회
        val commentIds = pagedRootComments.mapNotNull { it.id }
        val userInteractions =
            if (userId > 0 && commentIds.isNotEmpty()) {
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

        // 7. CommentResult 변환
        val commentResults =
            pagedRootComments.map { comment ->
                val userInteractionType = userInteractions[comment.id]
                val isLikedByUser = userInteractionType == InteractionType.LIKE

                CommentResult.from(
                    comment = comment,
                    userInteractionType = userInteractionType,
                    isLikedByUser = isLikedByUser,
                )
            }

        return FigureDetailsResult(
            id = figureResult.id!!,
            name = figureResult.name,
            imageUrl = figureResult.imageUrl,
            bio = figureResult.bio,
            sentimentCounts = sentimentCounts,
            commentsSize = pagedRootComments.size.toLong(),
            totalCommentCount = totalRootComments,
            comments = commentResults,
            userVote = userVote,
            categoryId = figureResult.category.id!!,
            categoryDisplayName = figureResult.category.displayName,
            categoryImage = figureResult.category.imageUrl ?: "",
            categoryDescription = figureResult.category.description ?: "",
            totalPages = totalPages,
            createdAt = figureResult.createdAt,
            updatedAt = figureResult.updatedAt,
        )
    }
}
