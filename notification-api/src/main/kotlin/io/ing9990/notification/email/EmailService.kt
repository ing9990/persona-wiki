package io.ing9990.notification.email

/**
 * 이메일 서비스 인터페이스
 * 다양한 이메일 발송 기능을 추상화합니다.
 */
interface EmailService {
    /**
     * HTML 형식의 이메일 발송
     * @param to 수신자 이메일
     * @param subject 이메일 제목
     * @param htmlContent HTML 형식의 이메일 내용
     */
    fun sendHtmlEmail(
        to: String,
        subject: String,
        htmlContent: String,
    )

    /**
     * 템플릿 기반 이메일 발송
     * @param to 수신자 이메일
     * @param subject 이메일 제목
     * @param templateName 템플릿 이름
     * @param variables 템플릿에 사용될 변수 맵
     */
    fun sendTemplateEmail(
        to: String,
        subject: String,
        templateName: String,
        variables: Map<String, Any>,
    )

    /**
     * 다수의 수신자에게 이메일 발송
     * @param recipients 수신자 이메일 목록
     * @param subject 이메일 제목
     * @param content 이메일 내용
     */
    fun sendBulkEmails(
        recipients: List<String>,
        subject: String,
        content: String,
    )
}
