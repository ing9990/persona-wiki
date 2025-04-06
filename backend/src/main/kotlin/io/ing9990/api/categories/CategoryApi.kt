package io.ing9990.api.categories

import io.ing9990.api.categories.dto.request.CategoryRequest
import io.ing9990.api.categories.dto.response.CategoryResponse
import io.ing9990.domain.category.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/categories")
class CategoryApi(
    private val categoryService: CategoryService,
) {
    @GetMapping
    fun getAllCategories(): ResponseEntity<List<CategoryResponse>> {
        val categories = categoryService.getAllCategories()
        return ResponseEntity.ok(categories.map { CategoryResponse.from(it) })
    }

    @PostMapping
    fun createCategory(
        @RequestBody request: CategoryRequest,
    ): ResponseEntity<CategoryResponse> {
        val category =
            categoryService.createCategory(
                id = request.id,
                displayName = request.displayName,
                description = request.description,
                imageUrl = request.imageUrl,
            )

        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(category))
    }
}
