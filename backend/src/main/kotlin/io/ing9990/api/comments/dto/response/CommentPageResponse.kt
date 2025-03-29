package io.ing9990.api.comments.dto.response

data class CommentPageResponse(
    val content: List<CommentResponse>,
    val totalPages: Int,
    val totalElements: Long,
    val currentPage: Int,
    val size: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)
