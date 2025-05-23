package io.ing9990.domain.figure.repository.querydsl

class SlugAndCategoryNotFound(
    val categoryId: String,
    val slug: String,
    message: String = "해당하는 인물을 찾을 수 없습니다.",
) : RuntimeException()
