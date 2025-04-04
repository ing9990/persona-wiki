package io.ing9990.domain.figure.repository

import io.ing9990.domain.figure.Figure
import io.ing9990.domain.figure.repository.querydsl.figure.FigureCustomRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 인물 관련 데이터 액세스를 위한 Repository 인터페이스
 */
interface FigureRepository :
    JpaRepository<Figure, Long>,
    FigureCustomRepository {
    /**
     * 카테고리와 함께 모든 인물을 조회합니다.
     */
    @Query("SELECT f FROM figure f LEFT JOIN FETCH f.category ORDER BY f.reputation.likeCount + f.reputation.dislikeCount DESC")
    fun findAllWithCategory(): List<Figure>

    /**
     * 카테고리 ID로 인물 목록을 조회합니다.
     */
    @Query("SELECT f FROM figure f JOIN FETCH f.category WHERE f.category.id = :categoryId")
    fun findByCategoryId(categoryId: String): List<Figure>

    /**
     * 카테고리 ID와 인물 이름으로 인물이 존재하는지 확인합니다.
     */
    @Query("SELECT (count(f) > 0) FROM figure f WHERE f.category.id = ?1 AND f.name = ?2")
    fun existsByCategoryIdAndName(
        categoryId: String,
        name: String,
    ): Boolean

    /**
     * 이름에 특정 문자열이 포함된 인물을 조회합니다.
     */
    @Query(
        """
        SELECT f 
        FROM figure f 
        LEFT JOIN FETCH f.category 
        WHERE f.name LIKE CONCAT('%', :name, '%')
    """,
    )
    fun findByNameContaining(name: String): List<Figure>

    /**
     * 카테고리를 JOIN FETCH로 함께 로드하여 인물 목록 조회
     */
    @Query("SELECT f FROM figure f JOIN FETCH f.category ORDER BY f.createdAt DESC")
    fun findAllWithCategoryFetchJoin(pageable: Pageable): Page<Figure>

    /**
     * 모든 인물을 카테고리와 함께 로드합니다.
     */
    @EntityGraph(attributePaths = ["category"])
    override fun findAll(pageable: Pageable): Page<Figure>
}
