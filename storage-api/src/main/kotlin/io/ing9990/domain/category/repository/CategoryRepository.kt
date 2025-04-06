package io.ing9990.domain.category.repository

import io.ing9990.domain.category.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * 카테고리 관련 데이터 액세스를 위한 Repository 인터페이스
 */
interface CategoryRepository : JpaRepository<Category, String> {
    /**
     * 카테고리 표시 이름으로 카테고리를 조회합니다.
     */
    @Query("select c from category c where c.displayName = ?1")
    fun findByDisplayName(displayName: String): Category?

    /**
     * 카테고리 표시 이름이 존재하는지 확인합니다.
     */
    @Query("select (count(c) > 0) from category c where c.displayName = ?1")
    fun existsByDisplayName(displayName: String): Boolean
}
