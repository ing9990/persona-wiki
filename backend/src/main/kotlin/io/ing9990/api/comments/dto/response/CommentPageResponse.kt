package io.ing9990.api.comments.dto.response

import io.ing9990.domain.comment.repository.querydsl.dto.CommentResult
import org.springframework.data.domain.Page

/**
 * 댓글 페이지 응답을 위한 DTO
 * 무한 스크롤을 구현하기 위한 페이지네이션 정보를 포함
 */
data class CommentPageResponse(
    val content: List<CommentResponse>,
    val pageable: PageableResponse,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val number: Int,
    val first: Boolean,
    val numberOfElements: Int,
    val empty: Boolean,
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
