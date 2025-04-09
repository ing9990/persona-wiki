package io.ing9990.domain.figure.service

import io.ing9990.domain.figure.service.dto.CreateFiureData

class CreateFigureException(
    message: String,
    val figureData: CreateFiureData? = null,
) : RuntimeException(message)
