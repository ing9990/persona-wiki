package io.ing9990.exceptions

import io.ing9990.domain.EntityNotFoundException
import io.ing9990.exceptions.ErrorCode.ENTITY_NOT_FOUND
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.coyote.BadRequestException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionAdvice {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private fun baseHandler(e: BussinessException): ResponseEntity.BodyBuilder = ResponseEntity.status(e.errorCode.httpStatus)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(e: EntityNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(NOT_FOUND)
            .body(ErrorResponse.from(ENTITY_NOT_FOUND))

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<ErrorResponse> =
        baseHandler(e).body(ErrorResponse.from(e.errorCode))

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse?> {
        val errorResponse = ErrorResponse.from(ErrorCode.INVALID_REQUEST_BODY)
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .unprocessableEntity()
            .body(ErrorResponse.of(ErrorCode.UNPROCESSABLE_ENTITY, e.bindingResult))

    @ExceptionHandler(
        BadRequestException::class,
        BussinessException::class,
    )
    fun handleBussinessException(e: BussinessException): ResponseEntity<ErrorResponse> =
        baseHandler(e).body(ErrorResponse.from(e.errorCode))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        log.error(e.message, e)

        return ResponseEntity
            .status(500)
            .body(
                ErrorResponse.from(
                    ErrorCode.ILLEGAL_ARGUMENT,
                ),
            )
    }

    @ExceptionHandler(MissingRequestCookieException::class)
    fun handleMissingRequestCookieException(e: MissingRequestCookieException): ResponseEntity<ErrorResponse> {
        log.warn("MissingRequestCookieException", e)

        return ResponseEntity
            .status(e.statusCode)
            .body(ErrorResponse.from(ErrorCode.NOT_FOUND_COOKIE))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun httpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(BAD_REQUEST)
            .body(ErrorResponse.from(ErrorCode.INVALID_REQUEST_METHOD))

    @ExceptionHandler(NoResourceFoundException::class)
    fun noResourceFoundException(
        e: NoResourceFoundException,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(BAD_REQUEST)
            .body(ErrorResponse.from(ErrorCode.NOT_FOUND_ENDPOINT))

    @ExceptionHandler(Exception::class)
    fun unHandledExceptionHandler(
        e: Exception?,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<ErrorResponse> {
        log.error("Not Expected Exception is Occurred", e)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.from(ErrorCode.UNHANDLED_EXCEPTION))
    }
}
