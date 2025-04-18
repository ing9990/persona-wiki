package io.ing9990.notification.email.templates.usecase

import io.ing9990.notification.email.EmailService
import io.ing9990.notification.email.templates.ReplyNotificationTemplate

class ReplyNotificationEmailUseCase(
    emailService: EmailService,
    override val recipient: String,
    username: String,
    figureId: Long,
    figureName: String,
    categoryId: String,
    commentContent: String,
    replyContent: String,
    replyAuthor: String,
) : BaseEmailUseCase(emailService) {
    override val subject: String = "[인물: $figureName]에 남긴 회원님의 댓글에 답글이 등록되었습니다."

    override val template: ReplyNotificationTemplate =
        createTemplate(
            username,
            figureId,
            figureName,
            categoryId,
            commentContent,
            replyContent,
            replyAuthor,
        )

    private fun createTemplate(
        username: String,
        figureId: Long,
        figureName: String,
        categoryId: String,
        commentContent: String,
        replyContent: String,
        replyAuthor: String,
    ): ReplyNotificationTemplate {
        val template = ReplyNotificationTemplate()
        template.addVariables(
            mapOf(
                "username" to username,
                "figureName" to figureName,
                "figureId" to figureId,
                "categoryId" to categoryId,
                "commentContent" to commentContent,
                "replyContent" to replyContent,
                "replyAuthor" to replyAuthor,
                "figureUrl" to "https://xn--3e0b39y4pd92v9pct9c.com/$categoryId/@$figureName",
            ),
        )
        return template
    }
}
