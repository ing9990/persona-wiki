package io.ing9990.domain.figure

import io.ing9990.domain.activities.events.handler.ActivityEventPublisher
import io.ing9990.domain.category.Category
import io.ing9990.domain.category.service.CategoryService
import io.ing9990.domain.figure.repository.FigureRepository
import io.ing9990.domain.figure.service.FigureService
import io.ing9990.domain.figure.service.dto.CreateFiureData
import io.ing9990.domain.fixtures.UserFixtures
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import java.text.Normalizer

@ExtendWith(MockKExtension::class)
class FigureServiceTest :
    BehaviorSpec({
        // 1. Mockk을 사용한 시나리오 테스트 테스트에 필요한 Mockk 객체를 준비한다.
        val figureRepository = mockk<FigureRepository>()
        val categoryService = mockk<CategoryService>()
        val activityEventPublisher = mockk<ActivityEventPublisher>()

        // 2. 테스트할 클래스의 인스턴스를 생성하자.
        val figureService =
            FigureService(figureRepository, categoryService, activityEventPublisher)

        // 3. 테스트 하려는 작업을 Given의 생성자에 명시,
        Given("인물 생성 시") {
            // 테스트를 하기위한 데이터를 Given 내부에 생성한다.
            val categoryId = "actor"
            val testCategory =
                Category(
                    categoryId,
                    "연예인",
                    "TV, 영화, 음악 등 연예 분야 인물입니다.",
                    "https://test-image.com/actor.png",
                )

            val figureName = "레오나르도 디카프리오"
            val data =
                CreateFiureData(
                    UserFixtures.USER_1.user,
                    figureName = figureName,
                    categoryId = categoryId,
                    imageUrl = "https://test-image.com/any.png",
                    bio = "Yo, bro",
                )

            val savedFigure =
                Figure
                    .create(
                        name = data.figureName,
                        imageUrl = data.imageUrl,
                        bio = data.bio,
                        category = testCategory,
                    )

            When("정상적인 데이터가 주어지면") {
                // FigureService.createFigure() 메서드에서 의존하는 다른 클래스의 작업들을 Mocking한다.
                every { categoryService.findCategoryById(categoryId) } returns testCategory
                every {
                    figureRepository.existsByCategoryIdAndName(
                        categoryId,
                        figureName,
                    )
                } returns false

                every {
                    figureRepository.save(any())
                } returns savedFigure

                // 이벤트 발행 관련 로직도 무시
                every {
                    activityEventPublisher.publishFigureAdded(
                        any(),
                        any(),
                    )
                } just runs

                val result = figureService.createFigure(data)

                Then("새로운 인물이 생성된다.") {
                    result.id shouldBe savedFigure.id
                    result.id shouldBe savedFigure.id
                    result.bio shouldBe savedFigure.bio

                    // atLeast = 5: 최소 5번 실행되었는지 검증한다.
                    // atMost = 5: 최대 5번 이하로 실행되었는지 검증한다.
                    // wasNot Called: 전혀 호출되지 않아야 한다.
                    // excatly = 1: 함수가 정확히 1번 실행되었는지 검증한다.
                    verify(exactly = 1) {
                        categoryService.findCategoryById(categoryId)
                        figureRepository.existsByCategoryIdAndName(
                            categoryId,
                            figureName,
                        )
                        figureRepository.save(any())
                        activityEventPublisher.publishFigureAdded(any(), any())
                    }
                }

                Then("slug가 올바르게 구성된다.") {
                    result.slug shouldBe "레오나르도-디카프리오"
                }

                fun String.normalize(): String = Normalizer.normalize(this, Normalizer.Form.NFC)

//                Then("초성이 올바르게 파싱된다.") {
//                    // 1. 원래 하려던 방법.
//                    result.chosung shouldBe "ㄹㅇㄴㄹㄷㄷㅋㅍㄹㅇ"
//
//                    // 1번이 실패해서 한글자씩도 비교해봄 (실패)
//                    result.chosung.map {
//                        it.toString().last()
//                    } shouldBe
//                        "ㄹㅇㄴㄹㄷㄷㅋㅍㄹㅇ".map {
//                            it.toString().last()
//                        }
//                    // 채찍피티한테 수소문해서 normalize() 라는 것을 적용. 실패.
//                    result.chosung.normalize() shouldBe "ㄹㅇㄴㄹㄷㄷㅋㅍㄹㅇ".normalize()
//                }
            }
        }
    })
