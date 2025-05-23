package io.ing9990.notification.email.templates.usecase.reply.dto

data class ReplyNotificationDto(
    val recipientEmail: String,
    val recipientUsername: String,
    val figureId: Long,
    val figureName: String,
    val categoryId: String,
    val commentContent: String,
    val replyContent: String,
    val replyAuthorName: String,
) {
    companion object {
        fun of(
            recipientEmail: String?,
            recipientUsername: String,
            figureId: Long,
            figureName: String,
            categoryId: String,
            commentContent: String,
            replyContent: String,
            replyAuthorName: String,
        ): ReplyNotificationDto {
            require(!recipientEmail.isNullOrBlank()) { "이메일 주소가 없는 사용자이기 때문에 이메일을 발송할 수 없습니다." }

            return ReplyNotificationDto(
                recipientEmail = recipientEmail,
                recipientUsername = recipientUsername,
                figureId = figureId,
                figureName = figureName,
                categoryId = categoryId,
                commentContent = commentContent,
                replyContent = replyContent,
                replyAuthorName = replyAuthorName,
            )
        }
    }
}
