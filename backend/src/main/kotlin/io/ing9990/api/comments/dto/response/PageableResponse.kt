package io.ing9990.api.comments.dto.response

import org.springframework.data.domain.Pageable

/**
 * 페이지 정보 응답을 위한 DTO
 */
data class PageableResponse(
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean,
) {
    companion object {
        fun from(pageable: Pageable): PageableResponse =
            PageableResponse(
                pageNumber = pageable.pageNumber,
                pageSize = pageable.pageSize,
                offset = pageable.offset,
                paged = true,
                unpaged = false,
            )
    }
}
