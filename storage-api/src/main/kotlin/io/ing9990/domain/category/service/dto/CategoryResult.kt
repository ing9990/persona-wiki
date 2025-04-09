package io.ing9990.domain.category.service.dto

import io.ing9990.domain.category.Category

data class CategoryResult(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
) {
    companion object {
        fun from(category: Category): CategoryResult =
            CategoryResult(
                category.id,
                category.displayName,
                category.imageUrl,
                category.description,
            )
    }
}
