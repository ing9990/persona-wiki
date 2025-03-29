package io.ing9990.domain.figure.service

import io.ing9990.domain.figure.Category
import io.ing9990.domain.figure.Comment
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.Sentiment
import io.ing9990.domain.figure.repository.CategoryRepository
import io.ing9990.domain.figure.repository.CommentRepository
import io.ing9990.domain.figure.repository.FigureRepository
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

    /**
     * 인물 이름으로 검색합니다. 카테고리가 함께 로딩됩니다.
     * @param name 검색할 인물 이름
     */
    fun searchByName(name: String): List<Figure> {
        return figureRepository.findByNameContaining(name)
    }

    /**
     * 초성 또는 일반 텍스트로 인물 이름을 검색합니다.
     * 예: "ㅇㅅㄹ"로 검색하면 "윤석열"을 찾을 수 있습니다.
     * @param query 검색어 (일반 텍스트 또는 초성)
     */
    fun searchByNameWithInitials(query: String): List<Figure> {
        // 쿼리가 비어있으면 빈 리스트 반환
        if (query.isBlank()) {
            return emptyList()
        }

        // 모든 인물 목록 가져오기 (카테고리 함께 로딩)
        val allFigures = figureRepository.findAllWithCategory()

        // 초성이 포함된 쿼리인지 확인
        val hasChosung = query.any { it in CHOSUNG_MAP.keys }

        return if (hasChosung) {
            // 초성 검색 로직
            allFigures.filter { figure ->
                matchesWithChosung(figure.name, query)
            }
        } else {
            // 일반 텍스트 검색 (기존 방식)
            figureRepository.findByNameContaining(query)
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
    ) {
        val comment =
            commentRepository.findByIdOrNull(commentId)
                ?: throw IllegalArgumentException("해당 ID의 댓글이 존재하지 않습니다: $commentId")

        if (isLike) {
            comment.likes++
        } else {
            comment.dislikes++
        }
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

    companion object {
        // 한글 초성 매핑
        private val CHOSUNG_MAP =
            mapOf(
                'ㄱ' to Regex("^[가-깋]"),
                'ㄲ' to Regex("^[까-낗]"),
                'ㄴ' to Regex("^[나-닣]"),
                'ㄷ' to Regex("^[다-딯]"),
                'ㄸ' to Regex("^[따-띻]"),
                'ㄹ' to Regex("^[라-맇]"),
                'ㅁ' to Regex("^[마-밓]"),
                'ㅂ' to Regex("^[바-빟]"),
                'ㅃ' to Regex("^[빠-삫]"),
                'ㅅ' to Regex("^[사-싷]"),
                'ㅆ' to Regex("^[싸-앃]"),
                'ㅇ' to Regex("^[아-잏]"),
                'ㅈ' to Regex("^[자-짛]"),
                'ㅉ' to Regex("^[짜-찧]"),
                'ㅊ' to Regex("^[차-칳]"),
                'ㅋ' to Regex("^[카-킿]"),
                'ㅌ' to Regex("^[타-팋]"),
                'ㅍ' to Regex("^[파-핗]"),
                'ㅎ' to Regex("^[하-힣]"),
            )

        /**
         * 인물 이름이 주어진 초성 패턴과 일치하는지 확인
         */
        private fun matchesWithChosung(
            name: String,
            chosungPattern: String,
        ): Boolean {
            // 이름에서 초성 추출
            val nameChosung = extractChosung(name)

            // 초성 패턴이 이름의 초성에 포함되는지 확인
            return nameChosung.startsWith(chosungPattern, ignoreCase = true) ||
                name.contains(chosungPattern, ignoreCase = true)
        }

        /**
         * 문자열에서 초성을 추출
         */
        private fun extractChosung(str: String): String {
            return str.map { char ->
                when {
                    char in 'ㄱ'..'ㅎ' -> char // 이미 초성인 경우
                    char in '가'..'힣' -> {
                        // 한글 유니코드 계산으로 초성 추출
                        val chosungIndex = (char.code - '가'.code) / 588
                        "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"[chosungIndex]
                    }

                    else -> char // 한글이 아닌 경우 그대로
                }
            }.joinToString("")
        }
    }
}
