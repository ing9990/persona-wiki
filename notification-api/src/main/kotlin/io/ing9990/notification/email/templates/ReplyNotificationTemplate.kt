package io.ing9990.notification.email.templates

class ReplyNotificationTemplate : BaseEmailTemplate() {
    override fun getTemplateName(): String = "emails/reply-notification"

    override fun getDefaultVariables(): Map<String, Any> =
        mapOf(
            "title" to "댓글 답글 알림",
            "previewText" to "회원님의 댓글에 새로운 답글이 등록되었습니다.",
        )

    override fun clone(): BaseEmailTemplate {
        val clone = ReplyNotificationTemplate()
        clone.variables.putAll(this.getAllVariables())
        return clone
    }
}
