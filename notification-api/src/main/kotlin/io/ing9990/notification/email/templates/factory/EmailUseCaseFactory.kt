package io.ing9990.notification.email.templates.factory

import io.ing9990.notification.email.EmailService
import io.ing9990.notification.email.templates.usecase.EmailUseCase
import io.ing9990.notification.email.templates.usecase.ReplyNotificationEmailUseCase
import io.ing9990.notification.email.templates.usecase.reply.dto.ReplyNotificationDto
import org.springframework.stereotype.Component

@Component
class EmailUseCaseFactory(
    private val emailService: EmailService,
) {
    fun createReplyNotificationUseCase(data: ReplyNotificationDto): EmailUseCase =
        ReplyNotificationEmailUseCase(
            emailService = emailService,
            recipient = data.recipientEmail,
            username = data.recipientUsername,
            figureId = data.figureId,
            figureName = data.figureName,
            categoryId = data.categoryId,
            commentContent = data.commentContent,
            replyContent = data.replyContent,
            replyAuthor = data.replyAuthorName,
        )
}
