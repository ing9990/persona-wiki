package io.ing9990.notification.email.event.data

/**
 * 댓글에 답글이 달렸을 때 이메일 알림을 위한 이벤트
 * notification-api 모듈에서만 구독하여 처리합니다.
 */
data class CommentReplyNotificationEvent(
    val recipientEmail: String,
    val recipientUsername: String,
    val figureId: Long,
    val figureName: String,
    val categoryId: String,
    val commentContent: String,
    val replyContent: String,
    val replyAuthorName: String,
    val timestamp: Long = System.currentTimeMillis(),
)
