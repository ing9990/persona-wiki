package io.ing9990.exceptions

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val httpStatus: HttpStatus,
    val code: Int,
    val message: String,
) {
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, 8001, "찾을 수 없는 리소스입니다."),
    ILLEGAL_ARGUMENT(HttpStatus.INTERNAL_SERVER_ERROR, 8002, "처리 중 오류가 발생했습니다."),

    // Request
    INVALID_PATH(HttpStatus.BAD_REQUEST, 9002, "잘못된 경로입니다."),
    UNPROCESSABLE_ENTITY(HttpStatus.BAD_REQUEST, 9003, "요청 데이터가 유효하지 않습니다."),
    INVALID_REQUEST_METHOD(HttpStatus.BAD_REQUEST, 9004, "잘못된 HTTP Method입니다."),
    NOT_FOUND_DATA(HttpStatus.BAD_REQUEST, 9005, "요청한 데이터를 찾을 수 없습니다."),
    NOT_FOUND_ENDPOINT(HttpStatus.BAD_REQUEST, 9006, "유효하지 않은 API 엔드포인트 입니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, 9007, "올바르지 않은 요청 데이터입니다."),
    NOT_FOUND_COOKIE(HttpStatus.BAD_REQUEST, 9008, "쿠키 정보를 찾을 수 없습니다."),

    // Authroization
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 4000, "로그인이 필요한 작업입니다."),

    // Uncatched,
    UNHANDLED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 9999, "예상치 못한 예외입니다."),
}
