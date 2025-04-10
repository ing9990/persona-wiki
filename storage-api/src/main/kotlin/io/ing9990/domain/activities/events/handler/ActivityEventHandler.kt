package io.ing9990.domain.activities.events.handler

import io.ing9990.domain.activities.Activity
import io.ing9990.domain.activities.events.CreateActivityEvent
import io.ing9990.domain.activities.repositories.ActivityRepository
import io.ing9990.domain.user.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class ActivityEventHandler(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger(ActivityEventHandler::class.java)

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleCreateActivityEvent(event: CreateActivityEvent) {
        try {
            val activityEvent = event.activityEvent

            // 사용자 조회
            val user =
                userRepository.findById(activityEvent.userId).orElseThrow {
                    IllegalArgumentException("User with ID ${activityEvent.userId} not found")
                }

            // 활동 객체 생성
            val activity =
                Activity(
                    user = user,
                    activityType = activityEvent.activityType,
                    targetId = activityEvent.targetId,
                    targetName = activityEvent.targetName,
                    description = activityEvent.description,
                )

            // 활동 저장
            activityRepository.save(activity)

            logger.info("Activity recorded: ${activityEvent.activityType} by user ${user.nickname}")
        } catch (e: Exception) {
            logger.error("Failed to record activity", e)
        }
    }
}
