package io.ing9990.admin.repository

import io.ing9990.domain.figure.Figure
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * 어드민 모듈용 인물 관련 데이터 액세스를 위한 Repository 인터페이스
 */
@Repository
interface AdminFigureRepository : JpaRepository<Figure, Long> {
    /**
     * 카테고리와 함께 모든 인물을 조회합니다.
     * JPQL을 사용하여 N+1 문제를 방지합니다.
     */
    @Query("SELECT f FROM figure f LEFT JOIN FETCH f.category")
    fun findAllWithCategory(): List<Figure>

    /**
     * 카테고리와 함께 페이징된 인물 목록을 조회합니다.
     * EntityGraph를 사용하여 카테고리를 즉시 로딩합니다.
     */
    @EntityGraph(attributePaths = ["category"])
    override fun findAll(pageable: Pageable): Page<Figure>

    /**
     * ID로 인물과 카테고리를 함께 조회합니다.
     */
    @EntityGraph(attributePaths = ["category"])
    override fun findById(id: Long): java.util.Optional<Figure>

    /**
     * 이름에 특정 문자열이 포함된 인물을 검색합니다.
     * 카테고리도 함께 조회합니다.
     */
    @Query("SELECT f FROM figure f JOIN FETCH f.category WHERE f.name LIKE CONCAT('%', :name, '%')")
    fun findByNameContaining(
        @Param("name") name: String,
    ): List<Figure>

    /**
     * 카테고리 ID로 인물 목록을 조회합니다.
     * 카테고리도 함께 조회합니다.
     */
    @Query("SELECT f FROM figure f JOIN FETCH f.category WHERE f.category.id = :categoryId")
    fun findByCategoryId(
        @Param("categoryId") categoryId: String,
    ): List<Figure>

    /**
     * 카테고리 ID로 인물 개수를 조회합니다.
     */
    @Query("SELECT COUNT(f) FROM figure f WHERE f.category.id = :categoryId")
    fun countByCategoryId(
        @Param("categoryId") categoryId: String,
    ): Long

    /**
     * 카테고리 ID와 인물 이름으로 인물이 존재하는지 확인합니다.
     */
    @Query("SELECT COUNT(f) > 0 FROM figure f WHERE f.category.id = :categoryId AND f.name = :name")
    fun existsByCategoryIdAndName(
        @Param("categoryId") categoryId: String,
        @Param("name") name: String,
    ): Boolean

    /**
     * 최근 등록된 인물 목록을 조회합니다.
     * 카테고리도 함께 조회합니다.
     */
    @Query("SELECT f FROM figure f JOIN FETCH f.category ORDER BY f.createdAt DESC")
    fun findRecentFigures(pageable: Pageable): List<Figure>
}
