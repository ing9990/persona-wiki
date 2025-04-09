package io.ing9990.domain.comment.repository.querydsl.dto

data class ReplyResult(
    val id: Long,
    val content: String,
    val likes: Int,
    val dislikes: Int,
    val userName: String?,
)
