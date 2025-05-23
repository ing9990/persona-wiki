package io.ing9990.domain.figure.service.dto

data class FigureMicroResults(
    val data: List<FigureMicroResult>,
)

data class FigureMicroResult(
    val categoryName: String,
    val figureName: String,
    val figureImage: String,
    val categoryId: String,
)
