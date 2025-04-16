package io.ing9990.domain.category.service.dto

import io.ing9990.domain.category.Category
import java.time.LocalDateTime

data class CategoryResult(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(category: Category): CategoryResult =
            CategoryResult(
                id = category.id,
                name = category.displayName,
                imageUrl = category.imageUrl,
                description = category.description,
                createdAt = category.createdAt,
                updatedAt = category.updatedAt,
            )
    }
}
