package io.ing9990.notification.email.templates.usecase

import io.ing9990.notification.email.EmailService
import io.ing9990.notification.email.templates.BaseEmailTemplate
import io.ing9990.notification.email.templates.EmailTemplates

abstract class BaseEmailUseCase(
    protected val emailService: EmailService,
) : EmailUseCase {
    abstract val recipient: String
    abstract val subject: String
    abstract val template: EmailTemplates

    override fun execute() {
        emailService.sendTemplateEmail(
            to = recipient,
            subject = subject,
            templateName = template.getTemplateName(),
            variables = (template as BaseEmailTemplate).getAllVariables(),
        )
    }
}
