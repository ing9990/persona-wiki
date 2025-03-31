// web/src/main/kotlin/io/ing9990/web/exceptions/ApiException.kt
package io.ing9990.api

import org.springframework.http.HttpStatus

/**
 * API 요청 처리 중 발생하는 예외
 */
class ApiException(
    message: String,
    val status: HttpStatus? = HttpStatus.BAD_REQUEST,
) : RuntimeException(message)
