package io.ing9990.domain.figure.service

import io.ing9990.common.HangeulUtil
import io.ing9990.common.HangeulUtil.Companion.CHOSUNG_MAP
import io.ing9990.domain.EntityNotFoundException
import io.ing9990.domain.category.Category
import io.ing9990.domain.category.repository.CategoryRepository
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.comment.Comment
import io.ing9990.domain.comment.CommentType
import io.ing9990.domain.comment.repository.CommentInteractionRepository
import io.ing9990.domain.comment.repository.CommentRepository
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.repository.FigureRepository
import io.ing9990.domain.user.User
import io.ing9990.domain.vote.Vote
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 인물 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@Transactional(readOnly = true)
class FigureService(
    private val figureRepository: FigureRepository,
    private val commentRepository: CommentRepository,
    private val commentInteractionRepository: CommentInteractionRepository,
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
) {
    private val log: Logger = LoggerFactory.getLogger(FigureService::class.java)

    /**
     * ID로 인물 정보를 상세히 조회합니다.
     * QueryDSL의 fetch join으로 votes 컬렉션이 함께 로딩됩니다.
     */
    fun findById(id: Long): Figure =
        figureRepository.findByIdWithVotes(id)
            ?: throw IllegalArgumentException("해당 ID의 인물이 존재하지 않습니다: $id")

    /**
     * 인물의 상세 정보들을 페치 조인합니다.
     */
    fun findByCategoryIdAndNameWithDetails(
        categoryId: String,
        figureName: String,
    ): Figure =
        figureRepository.findByCategoryIdAndNameWithDetails(categoryId, figureName)
            ?: throw EntityNotFoundException(
                "Figure",
                "$categoryId/$figureName",
                "해당 인물을 찾을 수 없습니다.",
            )

    /**
     * 카테고리 ID로, 해당 카테고리에 속한 인물 목록을 조회합니다.
     * 이제 카테고리가 함께 로딩되므로 LazyInitializationException이 발생하지 않습니다.
     * @param categoryId 카테고리 ID
     */
    fun findByCategoryId(categoryId: String): List<Figure> = figureRepository.findByCategoryId(categoryId)

    /**
     * 카테고리별 인기 인물을 가져옵니다.
     * @param limit 각 카테고리별로 가져올 인물 수
     * @return 카테고리 ID를 키로, 인물 목록을 값으로 하는 맵
     */
    @Transactional(readOnly = true)
    fun getPopularFiguresByCategory(limit: Int = 3): Map<Category, List<Figure>> {
        // 인물이 많은 상위 카테고리 10개 조회
        val topCategories =
            categoryRepository
                .findAll()
                .map { category ->
                    val figureCount = figureRepository.findByCategoryId(category.id).size
                    Pair(category, figureCount)
                }.sortedByDescending { it.second }
                .take(10)
                .map { it.first }

        // 각 카테고리별 인기 인물 조회
        return topCategories.associateWith { category ->
            // 해당 카테고리의 모든 인물
            val figures = figureRepository.findByCategoryId(category.id)

            // 평판 점수 기준으로 정렬 후 limit 개수만큼 추출
            figures.sortedByDescending { it.reputation.total() }.take(limit)
        }
    }

    /**
     * 모든 인물 목록을 카테고리와 함께 조회합니다.
     * 이제 카테고리가 함께 로딩되므로 LazyInitializationException이 발생하지 않습니다.
     */
    fun findAllWithCategory(): List<Figure> = figureRepository.findAllWithCategory()

    /**
     * 인기 있는 인물 목록을 가져옵니다. (평판 투표 + 댓글 수 기준)
     * @param limit 가져올 인물 수
     * @return 인기 있는 인물 목록
     */
    @Transactional(readOnly = true)
    fun getPopularFigures(limit: Int = 6): List<Figure> {
        // 모든 인물을 가져와 카테고리와 함께 로드
        val allFigures = findAllWithCategory()

        // 인물별 댓글 수 계산
        return allFigures
            .map { figure ->
                // 인물 ID로 댓글 조회
                val commentCount =
                    figure.id?.let { figureId ->
                        commentRepository.countCommentsByFigureId(figureId)
                    } ?: 0

                // 평판 점수 계산 (투표 총합)
                val reputationScore = figure.reputation.total()

                // 인물과 점수를 페어로 반환
                Pair(figure, reputationScore + commentCount)
            }
            // 점수 기준 내림차순 정렬 후 limit 개수만큼 추출
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    /**
     * 이름으로 검색합니다.
     */
    fun searchByName(name: String): List<Figure> {
        try {
            return figureRepository.findByNameContaining(name)
        } catch (e: Exception) {
            println("인물 검색 중 오류 발생: ${e.message}")
            throw RuntimeException("인물 '$name' 검색 중 오류가 발생했습니다", e)
        }
    }

    /**
     * 이니셜로 검색합니다.
     */
    fun searchByNameWithInitials(query: String): List<Figure> {
        if (query.isBlank()) {
            throw IllegalArgumentException("검색어를 입력해주세요")
        }

        try {
            // 로직은 유지
            val allFigures = figureRepository.findAllWithCategory()
            val hasChosung = query.any { it in CHOSUNG_MAP.keys }

            return if (hasChosung) {
                allFigures.filter { figure ->
                    HangeulUtil.matchesWithChosung(figure.name, query)
                }
            } else {
                figureRepository.findByNameContaining(query)
            }
        } catch (e: Exception) {
            println("초성 검색 중 오류 발생: ${e.message}")
            throw RuntimeException("검색어 '$query'로 초성 검색 중 오류가 발생했습니다", e)
        }
    }

    /**
     * 새로운 인물을 생성합니다.
     * @param name 인물 이름
     * @param categoryId 카테고리 ID
     * @param imageUrl 이미지 URL
     * @param bio 인물 설명
     */
    @Transactional
    fun createFigure(
        name: String,
        categoryId: String,
        imageUrl: String?,
        bio: String?,
    ): Figure {
        val category = categoryService.findCategoryById(categoryId)

        // 중복 체크
        if (figureRepository.existsByCategoryIdAndName(categoryId, name)) {
            throw IllegalArgumentException("이미 '$categoryId' 카테고리에 '$name' 인물이 존재합니다.")
        }

        val figure =
            Figure(
                name = name,
                imageUrl = imageUrl,
                bio = bio,
                category = category,
            )

        return figureRepository.save(figure)
    }

    // 새 메서드 추가
    @Transactional(readOnly = true)
    fun getCommentsByFigureIdWithUserInteractions(
        figureId: Long,
        userId: Long?,
        page: Int,
        size: Int,
    ): Page<Comment> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        return commentRepository.findCommentsWithUserInteractions(figureId, userId, pageable)
    }

    @Transactional(readOnly = true)
    fun getRepliesWithUserInteractions(
        parentId: Long,
        userId: Long?,
    ): List<Comment> = commentRepository.findRepliesWithUserInteractions(parentId, userId)

    /**
     * 사용자의 투표 정보를 가져옵니다.
     */
    fun getUserVote(
        figureId: Long,
        userId: Long?,
    ): Vote? {
        val figure = findById(figureId)
        return userId?.let { id -> figure.getUserVote(id) }
    }

    /**
     * 인물에 대한 댓글을 페이지 단위로 조회합니다.
     * @param figureId 인물 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 댓글 페이지
     */
    @Transactional(readOnly = true)
    fun getCommentsByFigureId(
        figureId: Long,
        page: Int,
        size: Int,
    ): Page<Comment> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        return commentRepository.findByFigureId(figureId, pageable)
    }

    /**
     * 댓글에 답글을 추가합니다.
     * @param parentCommentId 부모 댓글 ID
     * @param content 답글 내용
     * @param user 답글 작성자
     * @return 생성된 답글
     */
    @Transactional
    fun addReply(
        parentCommentId: Long,
        content: String,
        user: User,
    ): Comment {
        val parentComment =
            commentRepository.findByIdOrNull(parentCommentId)
                ?: throw IllegalArgumentException("해당 ID의 댓글이 존재하지 않습니다: $parentCommentId")

        // 최상위 부모(root) 댓글 ID 결정
        val rootId =
            if (parentComment.isRootComment()) {
                parentComment.id
            } else {
                parentComment.rootId
            }

        // 댓글 깊이 결정 (부모 댓글의 깊이 + 1)
        val depth = parentComment.depth + 1

        // 제한된 깊이 체크 (최대 3단계까지만 허용)
        if (depth > 3) {
            throw IllegalArgumentException("더 이상 답글을 달 수 없습니다. 최대 깊이에 도달했습니다.")
        }

        // 새 답글 생성
        val reply =
            Comment(
                figure = parentComment.figure,
                content = content,
                parent = parentComment,
                depth = depth,
                rootId = rootId,
                commentType = CommentType.REPLY,
                user = user,
            )

        // 부모 댓글에 답글 추가
        parentComment.addReply(reply)

        return commentRepository.save(reply)
    }

    /**
     * 부모 댓글 ID로 모든 답글을 사용자 정보와 함께 조회합니다.
     * QueryDSL을 사용하여 사용자 정보를 함께 로딩합니다.
     *
     * @param parentId 부모 댓글 ID
     * @return 사용자 정보가 로드된 답글 목록
     */
    fun getRepliesWithUserByParentId(parentId: Long): List<Comment> = commentRepository.findRepliesWithUserByParentId(parentId)
}
