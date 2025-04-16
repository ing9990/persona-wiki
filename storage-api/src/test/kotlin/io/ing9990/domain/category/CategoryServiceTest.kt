package io.ing9990.domain.category

import io.ing9990.domain.category.repository.CategoryRepository
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.category.service.dto.CreateCategoryData
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Optional

@ExtendWith(MockKExtension::class)
class CategoryServiceTest :
    BehaviorSpec(
        {
            val categoryRepository = mockk<CategoryRepository>()
            val categoryService = CategoryService(categoryRepository)

            Given("카테고리 생성 시") {
                val categoryId = "historical"
                val displayName = "위인"
                val description = "역사적 인물입니다."
                val imageUrl = "https://image.com/historical.png"

                val data: CreateCategoryData =
                    CreateCategoryData(
                        categoryId,
                        displayName,
                        description,
                        imageUrl,
                    )

                val savedCategory =
                    Category(
                        id = categoryId,
                        displayName = displayName,
                        description = description,
                        imageUrl = imageUrl,
                    )

                When("올바른 데이터가 주어지면") {
                    clearMocks(categoryRepository)

                    every { categoryRepository.existsById(categoryId) } returns false
                    every { categoryRepository.existsByDisplayName(displayName) } returns false
                    every { categoryRepository.save(any<Category>()) } returns savedCategory

                    val result = categoryService.createCategory(data)

                    Then("카테고리가 성공적으로 생성된다") {
                        result.id shouldBe categoryId
                        result.name shouldBe displayName
                        result.description shouldBe description
                        result.imageUrl shouldBe imageUrl

                        // private validateCreateCategory 메서드가 간접적으로 호출되었는지 검증
                        verify(exactly = 1) {
                            categoryRepository.existsById(categoryId)
                            categoryRepository.existsByDisplayName(displayName)
                            categoryRepository.save(any<Category>())
                        }
                    }
                }

                When("이미 존재하는 카테고리 ID가 주어지면") {
                    clearMocks(categoryRepository)

                    every { categoryRepository.existsById(categoryId) } returns true

                    Then("IllegalArgumentException 예외가 발생한다") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                categoryService.createCategory(data)
                            }

                        exception.message shouldContain "이미 존재하는 카테고리 ID입니다"

                        // private validateCreateCategory 메서드의 ID 검증 부분만 호출되고 이후 로직은 실행되지 않았는지 검증
                        verify(exactly = 1) {
                            categoryRepository.existsById(categoryId)
                        }

                        // 다음 검증 단계와 저장 로직은 실행되지 않아야 함
                        verify(exactly = 0) {
                            categoryRepository.existsByDisplayName(any())
                            categoryRepository.save(any())
                        }
                    }
                }

                When("이미 존재하는 카테고리 표시 이름이 주어지면") {
                    clearMocks(categoryRepository)

                    // ID는 존재하지 않지만 displayName이 이미 존재한다고 가정
                    every { categoryRepository.existsById(categoryId) } returns false
                    every { categoryRepository.existsByDisplayName(displayName) } returns true

                    Then("IllegalArgumentException 예외가 발생한다") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                categoryService.createCategory(data)
                            }

                        exception.message shouldContain "이미 존재하는 카테고리 표시 이름입니다"

                        // ID 검증은 통과했지만 displayName 검증에서 실패했는지 확인
                        verify(exactly = 1) {
                            categoryRepository.existsById(categoryId)
                            categoryRepository.existsByDisplayName(displayName)
                        }

                        // 저장 로직은 실행되지 않아야 함
                        verify(exactly = 0) {
                            categoryRepository.save(any())
                        }
                    }
                }
            }

            Given("카테고리 조회 시") {
                val categoryId = "historical"
                val displayName = "위인"
                val description = "역사적 인물입니다."
                val imageUrl = "https://image.com/historical.png"

                val category =
                    Category(
                        id = categoryId,
                        displayName = displayName,
                        description = description,
                        imageUrl = imageUrl,
                    )

                When("존재하는 카테고리 ID로 조회하면") {
                    clearMocks(categoryRepository)

                    every { categoryRepository.findById(categoryId) } returns Optional.of(category)

                    val result = categoryService.findCategoryById(categoryId)

                    Then("해당 카테고리가 반환된다") {
                        result.id shouldBe categoryId
                        result.displayName shouldBe displayName
                        result.description shouldBe description
                        result.imageUrl shouldBe imageUrl
                    }
                }

                When("존재하지 않는 카테고리 ID로 조회하면") {
                    clearMocks(categoryRepository)

                    val nonExistentId = "nonexistent"
                    every { categoryRepository.findById(nonExistentId) } returns Optional.empty()

                    Then("IllegalArgumentException 예외가 발생한다") {
                        val exception =
                            shouldThrow<IllegalArgumentException> {
                                categoryService.findCategoryById(nonExistentId)
                            }

                        exception.message shouldContain "해당 ID의 카테고리가 존재하지 않습니다"
                    }
                }
            }
        },
    )
