package io.ing9990.domain.figure.service

import io.ing9990.common.HangeulUtil
import io.ing9990.common.HangeulUtil.Companion.CHOSUNG_MAP
import io.ing9990.domain.figure.Category
import io.ing9990.domain.figure.Comment
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.Sentiment
import io.ing9990.domain.figure.repository.CategoryRepository
import io.ing9990.domain.figure.repository.CommentRepository
import io.ing9990.domain.figure.repository.FigureRepository
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
    private val categoryRepository: CategoryRepository,
    private val commentRepository: CommentRepository,
) {
    /**
     * 모든 인물 목록을 카테고리와 함께 조회합니다.
     * 이제 카테고리가 함께 로딩되므로 LazyInitializationException이 발생하지 않습니다.
     */
    fun findAllWithCategory(): List<Figure> {
        return figureRepository.findAllWithCategory()
    }

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
        return allFigures.map { figure ->
            // 인물 ID로 댓글 조회
            val commentCount = figure.id?.let { figureId ->
                commentRepository.findByFigureIdOrderByCreatedAtDesc(figureId).size
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
     * 카테고리별 인기 인물을 가져옵니다.
     * @param limit 각 카테고리별로 가져올 인물 수
     * @return 카테고리 ID를 키로, 인물 목록을 값으로 하는 맵
     */
    @Transactional(readOnly = true)
    fun getPopularFiguresByCategory(limit: Int = 3): Map<Category, List<Figure>> {
        // 인물이 많은 상위 카테고리 10개 조회
        val topCategories = categoryRepository.findAll()
            .map { category ->
                val figureCount = figureRepository.findByCategoryId(category.id).size
                Pair(category, figureCount)
            }
            .sortedByDescending { it.second }
            .take(10)
            .map { it.first }

        // 각 카테고리별 인기 인물 조회
        return topCategories.associateWith { category ->
            // 해당 카테고리의 모든 인물
            val figures = findByCategoryId(category.id)

            // 평판 점수 기준으로 정렬 후 limit 개수만큼 추출
            figures.sortedByDescending { it.reputation.total() }
                .take(limit)
        }
    }

    /**
     * ID로 인물 정보를 상세히 조회합니다.
     * 카테고리와 댓글이 함께 로딩됩니다.
     * @param id 인물 ID
     */
    fun findById(id: Long): Figure {
        return figureRepository.findByIdWithDetails(id)
            ?: throw IllegalArgumentException("해당 ID의 인물이 존재하지 않습니다: $id")
    }

    /**
     * 카테고리ID와 인물 이름으로 인물 정보를 상세히 조회합니다.
     * 카테고리와 댓글이 함께 로딩됩니다.
     * @param categoryId 카테고리 ID
     * @param figureName 인물 이름
     */
    fun findByCategoryIdAndNameWithDetails(
        categoryId: String,
        figureName: String,
    ): Figure? {
        return figureRepository.findByCategoryIdAndNameWithDetails(categoryId, figureName)
    }

    /**
     * 인물 ID로 댓글 목록을 조회합니다.
     * @param figureId 인물 ID
     */
    fun findCommentsByFigureId(figureId: Long): List<Comment> {
        return commentRepository.findByFigureIdOrderByCreatedAtDesc(figureId)
    }

    /**
     * 카테고리 ID로, 해당 카테고리에 속한 인물 목록을 조회합니다.
     * 이제 카테고리가 함께 로딩되므로 LazyInitializationException이 발생하지 않습니다.
     * @param categoryId 카테고리 ID
     */
    fun findByCategoryId(categoryId: String): List<Figure> {
        return figureRepository.findByCategoryId(categoryId)
    }

    /**
     * 카테고리 ID로 카테고리 정보를 조회합니다.
     * @param categoryId, 카테고리 ID
     */
    fun findCategoryById(categoryId: String): Category {
        return categoryRepository.findById(categoryId).orElse(null)
            ?: throw IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다: $categoryId")
    }

    fun searchByName(name: String): List<Figure> {
        try {
            return figureRepository.findByNameContaining(name)
        } catch (e: Exception) {
            // 오류 발생 시 로그 남기고 빈 목록 반환
            // 실제 환경에서는 로깅 프레임워크 사용 권장
            println("인물 검색 중 오류 발생: ${e.message}")
            return emptyList()
        }
    }

    // FigureService.kt 파일의 searchByNameWithInitials 메서드 수정
    fun searchByNameWithInitials(query: String): List<Figure> {
        // 쿼리가 비어있으면 빈 리스트 반환
        if (query.isBlank()) {
            return emptyList()
        }

        try {
            // 모든 인물 목록 가져오기 (카테고리 함께 로딩)
            val allFigures = figureRepository.findAllWithCategory()

            // 초성이 포함된 쿼리인지 확인
            val hasChosung = query.any { it in CHOSUNG_MAP.keys }

            return if (hasChosung) {
                // 초성 검색 로직
                allFigures.filter { figure ->
                    HangeulUtil.matchesWithChosung(figure.name, query)
                }
            } else {
                // 일반 텍스트 검색 (기존 방식)
                figureRepository.findByNameContaining(query)
            }
        } catch (e: Exception) {
            // 오류 발생 시 로그 남기고 빈 목록 반환
            println("초성 검색 중 오류 발생: ${e.message}")
            return emptyList()
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
        val category = findCategoryById(categoryId)

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

    /**
     * 새로운 댓글을 추가합니다.
     * @param figureId 인물 ID
     * @param content 댓글 내용
     */
    @Transactional
    fun addComment(
        figureId: Long,
        content: String,
    ): Comment {
        val figure = findById(figureId)

        val comment =
            Comment(
                figure = figure,
                content = content,
            )

        return commentRepository.save(comment)
    }

    /**
     * 댓글에 좋아요/싫어요를 추가합니다.
     * @param commentId 댓글 ID
     * @param isLike true면 좋아요, false면 싫어요
     */
    @Transactional
    fun likeOrDislikeComment(
        commentId: Long,
        isLike: Boolean,
    ): Comment {
        val comment =
            commentRepository.findByIdOrNull(commentId) ?: throw IllegalArgumentException(
                "해당 ID의 댓글이 존재하지 않습니다: $commentId",
            )

        if (isLike) {
            comment.likes++
        } else {
            comment.dislikes++
        }

        return comment
    }

    /**
     * 인물에 대한 평가(숭배/중립/사형)를 등록합니다.
     * @param figureId 인물 ID
     * @param sentiment 평가 감정 (POSITIVE, NEUTRAL, NEGATIVE)
     * @return 업데이트된 인물 객체
     */
    @Transactional
    fun voteFigure(
        figureId: Long,
        sentiment: Sentiment,
    ): Figure {
        val figure = findById(figureId)

        // 평가 타입에 따라 적절한 카운터 증가
        when (sentiment) {
            Sentiment.POSITIVE -> figure.reputation.likeCount++
            Sentiment.NEUTRAL -> figure.reputation.neutralCount++
            Sentiment.NEGATIVE -> figure.reputation.dislikeCount++
        }

        return figureRepository.save(figure)
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
}
