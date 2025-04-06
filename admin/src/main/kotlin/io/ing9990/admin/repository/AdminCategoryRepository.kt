package io.ing9990.admin.repository

import io.ing9990.domain.category.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

/**
 * 어드민 모듈용 카테고리 관련 데이터 액세스를 위한 Repository 인터페이스
 */
interface AdminCategoryRepository : JpaRepository<Category, String> {
    /**
     * 카테고리 표시 이름으로 카테고리를 조회합니다.
     */
    @Query("SELECT c FROM category c WHERE c.displayName = :displayName")
    fun findByDisplayName(
        @Param("displayName") displayName: String,
    ): Category?

    /**
     * 카테고리 표시 이름이 존재하는지 확인합니다.
     */
    @Query("SELECT COUNT(c) > 0 FROM category c WHERE c.displayName = :displayName")
    fun existsByDisplayName(
        @Param("displayName") displayName: String,
    ): Boolean

    /**
     * 사용되지 않는 카테고리 목록을 조회합니다. (인물이 없는 카테고리)
     */
    @Query("SELECT c FROM category c WHERE SIZE(c.figures) = 0")
    fun findUnusedCategories(): List<Category>
}
