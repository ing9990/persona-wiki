// web/src/main/kotlin/io/ing9990/web/exceptions/FigureOperationException.kt
package io.ing9990.web.exceptions

/**
 * 인물 추가, 수정, 삭제 등의 작업 시 발생하는 예외
 */
class FigureOperationException(
    message: String,
) : RuntimeException(message)
