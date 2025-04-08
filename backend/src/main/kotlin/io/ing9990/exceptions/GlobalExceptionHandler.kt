package io.ing9990.exceptions // web/src/main/kotlin/io/ing9990/web/exceptions/io.ing9990.exceptions.GlobalExceptionHandler.kt

import io.ing9990.aop.CurrentUser
import io.ing9990.aop.resolver.CurrentUserDto
import io.ing9990.api.ApiException
import io.ing9990.domain.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.support.RedirectAttributes

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

    /**
     * 인물 생성 또는 수정 관련 예외 처리
     */
    @ExceptionHandler(value = [FigureOperationException::class])
    fun handleFigureOperationException(
        e: FigureOperationException,
        redirectAttributes: RedirectAttributes,
    ): String {
        redirectAttributes.addFlashAttribute("error", e.message)
        return "redirect:/add-figure"
    }

    /**
     * API 요청 시 발생한 예외 처리
     */
    @ExceptionHandler(value = [ApiException::class])
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleApiException(e: ApiException): ResponseEntity<Map<String, String>> {
        e.printStackTrace()

        return ResponseEntity
            .status(e.status ?: HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to (e.message ?: "알 수 없는 오류가 발생했습니다")))
    }

    @ExceptionHandler(value = [UnauthorizedException::class])
    fun handleUnauthorizedException(
        e: UnauthorizedException,
        model: Model,
    ): ApiException = ApiException("인증이 필요한 작업입니다.", UNAUTHORIZED)

    /**
     * 일반적인 RuntimeException 처리
     * 서버 내부 오류 발생 시 500 페이지로 이동
     */
    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleRuntimeException(
        @CurrentUser currentUser: CurrentUserDto,
        e: RuntimeException,
        model: Model,
    ): String {
        e.printStackTrace()

        model.addAttribute("current", currentUser)
        model.addAttribute("error", e.message)
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value())

        return "error/500"
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(
        @CurrentUser currentUser: CurrentUserDto,
        e: Exception,
        model: Model,
    ): String {
        e.printStackTrace()

        model.addAttribute("error", e.message)
        model.addAttribute("current", currentUser)
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
        return "error/500"
    }
}
