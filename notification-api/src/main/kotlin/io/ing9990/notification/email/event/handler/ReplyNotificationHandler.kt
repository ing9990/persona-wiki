package io.ing9990.notification.email.event.handler

import io.ing9990.notification.email.event.data.CommentReplyNotificationEvent
import io.ing9990.notification.email.templates.factory.EmailUseCaseFactory
import io.ing9990.notification.email.templates.usecase.reply.dto.ReplyNotificationDto
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ReplyNotificationHandler(
    private val emailUseCaseFactory: EmailUseCaseFactory,
) {
    private val logger = LoggerFactory.getLogger(ReplyNotificationHandler::class.java)

    @Async("emailTaskExecutor")
    @EventListener
    fun handleCommentReplyNotificationEvent(event: CommentReplyNotificationEvent) {
        try {
            // 이벤트 데이터로부터 DTO 생성
            val notificationDto =
                ReplyNotificationDto.of(
                    recipientEmail = event.recipientEmail,
                    recipientUsername = event.recipientUsername,
                    figureId = event.figureId,
                    figureName = event.figureName,
                    categoryId = event.categoryId,
                    commentContent = event.commentContent,
                    replyContent = event.replyContent,
                    replyAuthorName = event.replyAuthorName,
                )

            // 이메일 발송 유스케이스 생성 및 실행
            val emailUseCase = emailUseCaseFactory.createReplyNotificationUseCase(notificationDto)
            emailUseCase.execute()

            logger.info("Reply notification email sent to: ${event.recipientEmail}")
        } catch (e: Exception) {
            logger.error("Failed to send reply notification: ${e.message}", e)
        }
    }
}
