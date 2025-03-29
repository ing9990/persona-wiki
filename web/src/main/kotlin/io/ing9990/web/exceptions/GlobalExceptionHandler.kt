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
    fun handleEntityNotFoundException(e: EntityNotFoundException, model: Model): String {
        model.addAttribute("error", e.message)
        model.addAttribute("entityType", e.entityType)
        model.addAttribute("identifier", e.identifier)
        model.addAttribute("status", HttpStatus.NOT_FOUND.value())

        return "error/404"
    }

    /**
     * 일반적인 RuntimeException 처리
     * 서버 내부 오류 발생 시 500 페이지로 이동
     */
    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleRuntimeException(e: RuntimeException, model: Model): String {
        model.addAttribute("error", e.message)
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value())

        return "error/500"
    }
}