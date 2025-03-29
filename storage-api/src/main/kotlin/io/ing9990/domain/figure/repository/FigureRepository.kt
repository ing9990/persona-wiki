package io.ing9990.domain.figure.repository

import io.ing9990.domain.figure.Figure
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 인물 관련 데이터 액세스를 위한 Repository 인터페이스
 */
interface FigureRepository : JpaRepository<Figure, Long> {

    /**
     * 카테고리와 함께 모든 인물을 조회합니다.
     * JPQL을 사용하여 N+1 문제를 방지합니다.
     */
    @Query("SELECT f FROM figure f LEFT JOIN FETCH f.category")
    fun findAllWithCategory(): List<Figure>

    /**
     * 카테고리 ID로 인물 목록을 조회합니다.
     * 카테고리도 함께 조회합니다.
     */
    @Query("SELECT f FROM figure f JOIN FETCH f.category WHERE f.category.id = :categoryId")
    fun findByCategoryId(categoryId: String): List<Figure>

    /**
     * 카테고리 ID와 인물 이름으로 인물을 조회합니다.
     * 카테고리만 함께 조회합니다 (댓글은 필요시 별도 로드).
     */
    @Query(
        "SELECT f FROM figure f " +
                "JOIN FETCH f.category " +
                "WHERE f.category.id = :categoryId AND f.name = :name"
    )
    fun findByCategoryIdAndNameWithDetails(categoryId: String, name: String): Figure?

    /**
     * 카테고리 ID와 인물 이름으로 인물이 존재하는지 확인합니다.
     */
    @Query("select (count(f) > 0) from figure f where f.category.id = ?1 and f.name = ?2")
    fun existsByCategoryIdAndName(categoryId: String, name: String): Boolean

    /**
     * 이름에 특정 문자열이 포함된 인물을 검색합니다.
     * 카테고리도 함께 조회합니다.
     */
    @Query("SELECT f FROM figure f JOIN FETCH f.category WHERE f.name LIKE CONCAT('%', :name, '%')")
    fun findByNameContaining(name: String): List<Figure>

    /**
     * ID로 인물을 조회하며, 카테고리만 함께 조회합니다.
     * 댓글은 성능을 위해 필요할 때 별도로 로드합니다.
     */
    @Query(
        "SELECT f FROM figure f " +
                "JOIN FETCH f.category " +
                "WHERE f.id = :id"
    )
    fun findByIdWithDetails(id: Long): Figure?
}