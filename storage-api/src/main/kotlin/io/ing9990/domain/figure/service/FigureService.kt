package io.ing9990.domain.figure.service

import io.ing9990.domain.EntityNotFoundException
import io.ing9990.domain.activities.events.handler.ActivityEventPublisher
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.repository.FigureRepository
import io.ing9990.domain.figure.service.dto.CreateFiureData
import io.ing9990.domain.figure.service.dto.FigureCardResult
import io.ing9990.domain.figure.service.dto.FigureDetailsResult
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
    private val categoryService: CategoryService,
    private val activityEventPublisher: ActivityEventPublisher,
) {
    private val log: Logger = LoggerFactory.getLogger(FigureService::class.java)

    /**
     * ID로 인물 정보를 상세히 조회합니다.
     * QueryDSL의 fetch join으로 votes 컬렉션이 함께 로딩됩니다.
     */
    fun findById(id: Long): Figure =
        figureRepository.findByIdWithVotes(id)
            ?: throw IllegalArgumentException("해당 ID의 인물이 존재하지 않습니다: $id")

    fun searchByCategoryIdAndName(
        categoryId: String,
        figureName: String,
    ) = figureRepository.findFigureByCategoryIdAndSlug(categoryId, figureName)
        ?: throw EntityNotFoundException(
            "Figure",
            "$categoryId/figureName",
            "해당 인물을 찾을 수 없습니다.",
        )

    fun searchByCategoryIdAndNameOrNull(
        categoryId: String,
        slug: String,
    ): FigureCardResult =
        FigureCardResult.from(
            figureRepository.findFigureByCategoryIdAndSlug(categoryId, slug)
                ?: throw EntityNotFoundException(
                    "Figure",
                    "$categoryId/figureName",
                    "해당 인물을 찾을 수 없습니다.",
                ),
        )

    /**
     * 인물의 상세 정보들을 페치 조인합니다.
     */
    fun findByCategoryIdAndNameWithDetails(
        categoryId: String,
        slug: String,
        userId: Long,
        page: Int,
        size: Int,
    ): FigureDetailsResult {
        val result: FigureDetailsResult =
            figureRepository.findByCategoryIdAndNameWithDetails(
                categoryId = categoryId,
                slug = slug,
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
    fun findByCategoryId(categoryId: String): List<FigureCardResult> =
        figureRepository
            .findByCategoryId(categoryId)
            .map { FigureCardResult.from(it) }

    /**
     * 인기 있는 인물 목록을 가져옵니다. (평판 투표 + 댓글 수 기준)
     * @param limit 가져올 인물 수
     * @return 인기 있는 인물 목록
     */
    @Transactional(readOnly = true)
    fun getPopularFigures(limit: Int): List<FigureCardResult> {
        val result: List<FigureCardResult> = figureRepository.findPopularFigues(limit)

        return result
    }

    /**
     * 이름으로 검색합니다.
     */
    fun searchByName(name: String): List<FigureCardResult> {
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
            throw CreateFigureException(
                "이미 '${data.categoryId}' 카테고리에 '${data.figureName}' 인물이 존재합니다.",
                data,
            )
        }

        val figure =
            Figure.create(
                name = data.figureName,
                imageUrl = data.imageUrl,
                bio = data.bio,
                category = category,
            )

        val savedFigure = figureRepository.save(figure)
        activityEventPublisher.publishFigureAdded(savedFigure, userId = data.user.id!!)
        return savedFigure
    }
}
