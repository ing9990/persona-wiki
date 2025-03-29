package io.ing9990.web.exception

import io.ing9990.web.exceptions.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 전역 예외 처리를 위한 컨트롤러 어드바이스
 */
@ControllerAdvice
class GlobalExceptionHandler {
    /**
     * EntityNotFoundException 처리
     * 엔티티를 찾을 수 없는 경우 404 페이지로 이동
     */
    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleEntityNotFoundException(
        e: EntityNotFoundException,
        model: Model,
    ): String {
        model.addAttribute("error", e.message)
        model.addAttribute("entityType", e.entityType)
        model.addAttribute("identifier", e.identifier)
        model.addAttribute("status", HttpStatus.NOT_FOUND.value())

        return "error/404"
    }

    // GlobalExceptionHandler.kt 파일에 아래 메서드 추가

    /**
     * 검색 관련 예외 처리
     * 검색 중 발생하는 예외를 처리하여 사용자에게 적절한 메시지를 보여줍니다.
     */
    @ExceptionHandler(value = [NoSuchElementException::class, IllegalArgumentException::class])
    fun handleSearchException(
        e: Exception,
        model: Model,
    ): String {
        model.addAttribute("error", "검색 중 오류가 발생했습니다: ${e.message}")
        model.addAttribute("query", "")
        model.addAttribute("searchResults", emptyList<Any>())

        return "search/search-results"
    }

    /**
     * 일반적인 RuntimeException 처리
     * 서버 내부 오류 발생 시 500 페이지로 이동
     */
    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleRuntimeException(
        e: RuntimeException,
        model: Model,
    ): String {
        model.addAttribute("error", e.message)
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value())

        return "error/500"
    }
}
