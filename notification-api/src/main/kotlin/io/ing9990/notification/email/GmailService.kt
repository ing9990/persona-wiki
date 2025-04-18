package io.ing9990.notification.email

import io.ing9990.notification.email.config.EmailConfig
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.nio.charset.StandardCharsets

@Service
class GmailService(
    private val config: EmailConfig,
    private val sender: JavaMailSender,
    private val templateEngine: TemplateEngine,
) : EmailService {
    private val logger = LoggerFactory.getLogger(GmailService::class.java)

    @Async
    override fun sendHtmlEmail(
        to: String,
        subject: String,
        htmlContent: String,
    ) {
        try {
            val message: MimeMessage = sender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, StandardCharsets.UTF_8.name())

            helper.setFrom(config.fromAddress)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(htmlContent, true)

            sender.send(message)
            logger.info("Successfully sent HTML email to: $to")
        } catch (e: Exception) {
            logger.error("Failed to send HTML email to: $to", e)
        }
    }

    @Async
    override fun sendTemplateEmail(
        to: String,
        subject: String,
        templateName: String,
        variables: Map<String, Any>,
    ) {
        try {
            logger.info("Starting to process template email: $templateName to: $to")
            logger.info("Variables size: ${variables.size}")

            // 모든 변수 로깅
            variables.forEach { (key, value) ->
                logger.info("Variable: $key = $value")
            }

            val context = Context()
            variables.forEach { (key, value) ->
                context.setVariable(key, value)
                logger.info("Set in context: $key = $value")
            }

            val htmlContent = templateEngine.process(templateName, context)

            if (htmlContent.isNullOrBlank()) {
                logger.error("Generated HTML content is empty or null")
            } else {
                logger.info("HTML content generated successfully (${htmlContent.length} characters)")
                // 생성된 HTML의 시작 부분을 로깅해서 변수 치환 여부 확인
                logger.debug("HTML preview: ${htmlContent.take(200)}...")
            }

            sendHtmlEmail(to, subject, htmlContent)
        } catch (e: Exception) {
            logger.error("Failed to send template email to: $to", e)
            logger.error("Template name: $templateName")
            logger.error("Variables: $variables")
        }
    }

    @Async
    override fun sendBulkEmails(
        recipients: List<String>,
        subject: String,
        content: String,
    ) {
        for (recipient in recipients) {
            sendHtmlEmail(recipient, subject, content)
        }
    }
}
