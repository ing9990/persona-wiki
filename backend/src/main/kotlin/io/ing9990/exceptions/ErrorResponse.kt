package io.ing9990.exceptions

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError

class ErrorResponse(
    @Schema(description = "에러 코드", example = "9999")
    private val errorCode: Int,
    @Schema(description = "에러 메시지", example = "요청 파라미터 누락")
    private val message: String,
    @Schema(description = "HTTP 상태", example = "INTERNAL_SERVER_ERROR")
    private val httpStatus: HttpStatus,
    private val errors: List<FieldError> = ArrayList(),
) {
    constructor(errorCode: ErrorCode) : this(
        errorCode.code,
        errorCode.message,
        errorCode.httpStatus,
    )

    constructor(code: ErrorCode, errors: List<FieldError>) : this(
        code.code,
        code.message,
        code.httpStatus,
        errors,
    )

    class FieldError(
        val field: String,
        val value: String,
        val reason: String,
    ) {
        companion object {
            fun of(
                field: String,
                value: String,
                reason: String,
            ): List<FieldError> =
                mutableListOf(
                    FieldError(
                        field = field,
                        value = value,
                        reason = reason,
                    ),
                )

            fun of(result: BindingResult): List<FieldError> =
                result.allErrors.map { error: ObjectError ->
                    val field = error as org.springframework.validation.FieldError
                    FieldError(
                        field = field.field,
                        reason = field.defaultMessage,
                        value = if (field.rejectedValue == null) "" else field.rejectedValue.toString(),
                    )
                }
        }
    }

    companion object {
        fun from(e: ErrorCode): ErrorResponse = ErrorResponse(e)

        fun of(
            code: ErrorCode,
            bindingResult: BindingResult,
        ): ErrorResponse = ErrorResponse(code, FieldError.of(bindingResult))
    }
}
