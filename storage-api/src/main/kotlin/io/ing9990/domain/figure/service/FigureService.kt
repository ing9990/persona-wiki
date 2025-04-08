package io.ing9990.domain.figure.service

import io.ing9990.domain.EntityNotFoundException
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.comment.repository.CommentRepository
import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.repository.FigureRepository
import io.ing9990.domain.figure.service.dto.CreateFiureData
import io.ing9990.domain.figure.service.dto.FigureCardResult
import io.ing9990.domain.figure.service.dto.FigureDetailsResult
import io.ing9990.domain.figure.service.dto.FigureMicroResults
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val categoryService: CategoryService,
) {
    private val log: Logger = LoggerFactory.getLogger(FigureService::class.java)

    /**
     * ID로 인물 정보를 상세히 조회합니다.
     * QueryDSL의 fetch join으로 votes 컬렉션이 함께 로딩됩니다.
     */
    fun findById(id: Long): Figure =
        figureRepository.findByIdWithVotes(id)
            ?: throw IllegalArgumentException("해당 ID의 인물이 존재하지 않습니다: $id")

    fun findFigureByCategoryIdAndName(
        categoryId: String,
        figureName: String,
    ) = figureRepository.findFigureByCategoryIdAndName(categoryId, figureName)
        ?: throw EntityNotFoundException(
            "Figure",
            "$categoryId/figureName",
            "해당 인물을 찾을 수 없습니다.",
        )

    /**
     * 인물의 상세 정보들을 페치 조인합니다.
     */
    fun findByCategoryIdAndNameWithDetails(
        categoryId: String,
        figureName: String,
        userId: Long,
        page: Int,
        size: Int,
    ): FigureDetailsResult {
        val result: FigureDetailsResult =
            figureRepository.findByCategoryIdAndNameWithDetails(
                categoryId = categoryId,
                figureName = figureName,
                userId = userId,
                commentPage = page,
                commentSize = size,
            )
        return result
    }

    /**
     * 카테고리 ID로, 해당 카테고리에 속한 인물 목록을 조회합니다.
     * 이제 카테고리가 함께 로딩되므로 LazyInitializationException이 발생하지 않습니다.
     * @param categoryId 카테고리 ID
     */
    fun findByCategoryId(categoryId: String): List<Figure> = figureRepository.findByCategoryId(categoryId)

    /**
     * 인기 있는 인물 목록을 가져옵니다. (평판 투표 + 댓글 수 기준)
     * @param limit 가져올 인물 수
     * @return 인기 있는 인물 목록
     */
    @Transactional(readOnly = true)
    fun getPopularFigures(limit: Int = 6): List<FigureCardResult> {
        val result: List<FigureCardResult> = figureRepository.findPopularFigues()

        return result
    }

    /**
     * 이름으로 검색합니다.
     */
    fun searchByName(name: String): FigureMicroResults {
        try {
            return figureRepository.searchByName(name)
        } catch (e: Exception) {
            log.error("인물 검색 중 오류 발생: ${e.message}")
            throw RuntimeException("인물 '$name' 검색 중 오류가 발생했습니다", e)
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
    fun createFigure(data: CreateFiureData): Figure {
        val category = categoryService.findCategoryById(data.categoryId)

        // 중복 체크
        if (figureRepository.existsByCategoryIdAndName(data.categoryId, data.figureName)) {
            throw IllegalArgumentException("이미 '${data.categoryId}' 카테고리에 '${data.figureName}' 인물이 존재합니다.")
        }

        val figure =
            Figure.create(
                name = data.figureName,
                imageUrl = data.imageUrl,
                bio = data.bio,
                category = category,
            )

        return figureRepository.save(figure)
    }

    @Transactional(readOnly = true)
    fun getRepliesWithUserInteractions(
        parentId: Long,
        userId: Long?,
    ): List<CommentResult> = commentRepository.findRepliesWithUserInteractions(parentId, userId)

    /**
     * 모든 인물 목록을 카테고리와 함께 조회합니다.
     * 이제 카테고리가 함께 로딩되므로 LazyInitializationException이 발생하지 않습니다.
     */
    fun findAllWithCategorySITEMAP(): List<Figure> = figureRepository.findAllWithCategoryPopularOrder()
}
