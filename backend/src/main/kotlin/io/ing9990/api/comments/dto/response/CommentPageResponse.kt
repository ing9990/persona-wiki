package io.ing9990.api.comments.dto.response

import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import org.springframework.data.domain.Page

/**
 * 댓글 페이지 응답을 위한 DTO
 * 무한 스크롤을 구현하기 위한 페이지네이션 정보를 포함
 */
data class CommentPageResponse(
    val content: List<CommentResponse>, // 현재 페이지의 댓글 목록
    val pageable: PageableResponse, // 페이지 정보
    val last: Boolean, // 마지막 페이지 여부
    val totalPages: Int, // 전체 페이지 수
    val totalElements: Long, // 전체 댓글 수
    val size: Int, // 페이지 크기
    val number: Int, // 현재 페이지 번호 (0-based)
    val first: Boolean, // 첫 페이지 여부
    val numberOfElements: Int, // 현재 페이지 댓글 수
    val empty: Boolean, // 빈 페이지 여부
) {
    companion object {
        fun from(page: Page<CommentResult>): CommentPageResponse =
            CommentPageResponse(
                content = page.content.map { CommentResponse.from(it) },
                pageable = PageableResponse.from(page.pageable),
                last = page.isLast,
                totalPages = page.totalPages,
                totalElements = page.totalElements,
                size = page.size,
                number = page.number,
                first = page.isFirst,
                numberOfElements = page.numberOfElements,
                empty = page.isEmpty,
            )
    }
}
