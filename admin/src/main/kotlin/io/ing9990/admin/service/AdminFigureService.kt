package io.ing9990.admin.service

import io.ing9990.admin.dto.FigureListDto
import io.ing9990.domain.category.repository.CategoryRepository
import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.repository.FigureRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminFigureService(
    private val figureRepository: FigureRepository,
    private val categoryRepository: CategoryRepository,
) {
    /**
     * 인물 개수를 조회한다.
     */
    fun getFigureCount(): Long = figureRepository.count()

    /**
     * 최근 추가된 인물을 조회한다.
     */
    fun getRecentFigures(limit: Int): List<Figure> =
        figureRepository
            .findAllWithCategory()
            .sortedByDescending { it.createdAt }
            .take(limit)

    /**
     * 모든 인물을 카테고리와 함께 조회한다.
     */
    fun getAllFigures(): List<Figure> = figureRepository.findAllWithCategory()

    /**
     * 인물을 페이징하여 조회한다.
     */
    fun getFiguresPaged(pageable: Pageable): Page<Figure> = figureRepository.findAll(pageable)

    /**
     * ID로 인물을 조회한다.
     */
    fun getFigureById(id: Long): Figure? = figureRepository.findByIdWithDetails(id)

    /**
     * 카테고리 ID로 인물 목록을 조회한다.
     */
    fun getFiguresByCategoryId(categoryId: String): List<Figure> = figureRepository.findByCategoryId(categoryId)

    /**
     * 인물 목록을 DTO로 변환하여 페이징 조회한다.
     */
    fun getFiguresPagedDto(pageable: Pageable): Page<FigureListDto> {
        // JPQL을 사용하여 카테고리를 함께 로드
        val figuresWithCategory = figureRepository.findAllWithCategoryFetchJoin(pageable)

        return figuresWithCategory.map { FigureListDto.from(it) }
    }

    /**
     * 새 인물을 생성한다.
     */
    @Transactional
    fun createFigure(
        name: String,
        categoryId: String,
        imageUrl: String?,
        bio: String?,
    ): Figure {
        val category =
            categoryRepository
                .findById(categoryId)
                .orElseThrow { IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다: $categoryId") }

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
     * 인물을 수정한다.
     */
    @Transactional
    fun updateFigure(
        id: Long,
        name: String,
        categoryId: String,
        imageUrl: String?,
        bio: String?,
    ): Figure {
        val figure =
            figureRepository.findByIdOrNull(id)
                ?: throw IllegalArgumentException("해당 ID의 인물이 존재하지 않습니다: $id")

        val category =
            categoryRepository
                .findById(categoryId)
                .orElseThrow { IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다: $categoryId") }

        // 동일 카테고리 내 다른 인물과 이름 중복 체크 (자신은 제외)
        if (categoryId != figure.category.id &&
            figureRepository.existsByCategoryIdAndName(
                categoryId,
                name,
            )
        ) {
            throw IllegalArgumentException("이미 '$categoryId' 카테고리에 '$name' 인물이 존재합니다.")
        }

        // 인물 정보 업데이트
        figure.name = name
        figure.category = category
        figure.imageUrl = imageUrl
        figure.bio = bio

        return figureRepository.save(figure)
    }

    /**
     * 인물을 삭제한다.
     */
    @Transactional
    fun deleteFigure(id: Long) {
        val figure =
            figureRepository.findByIdOrNull(id)
                ?: throw IllegalArgumentException("해당 ID의 인물이 존재하지 않습니다: $id")

        figureRepository.delete(figure)
    }

    /**
     * 인물을 검색한다.
     */
    fun searchFigures(keyword: String): List<Figure> = figureRepository.findByNameContaining(keyword)
}
