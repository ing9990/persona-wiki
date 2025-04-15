package io.ing9990.monitor.aop.clr

import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * 애플리케이션 시작 시 Slack으로 테스트 메시지를 보내는 컴포넌트
 * 슬랙 연동 테스트용으로만 사용하며, 정상 작동 확인 후 제거할 수 있습니다.
 */
@Component
@Profile("prod") // slack-test 프로필이 활성화된 경우에만 실행
class SlackTestSender : CommandLineRunner {
    private val logger = LoggerFactory.getLogger("slack.test")
    private val apiMonitorMarker = MarkerFactory.getMarker("API_MONITORING")
    private val mainLogger = LoggerFactory.getLogger(SlackTestSender::class.java)

    override fun run(vararg args: String?) {
        mainLogger.info("Slack 테스트 메시지 전송 시작...")

        // 일반 로거를 통한 테스트 메시지 (SIMPLE_SLACK으로 전송)
        logger.info("이것은 slack.test 로거를 통한 테스트 메시지입니다. (${System.currentTimeMillis()})")

        // API_MONITORING 마커를 통한 테스트 메시지
        mainLogger.info(
            apiMonitorMarker,
            """
            |*Slack 연동 테스트*
            |이것은 API_MONITORING 마커를 사용한 테스트 메시지입니다.
            |시간: ${System.currentTimeMillis()}
            |이 메시지가 보인다면 Slack 연동이 정상 작동하는 것입니다.
            """.trimMargin(),
        )

        mainLogger.info("Slack 테스트 메시지 전송 완료")
    }
}
