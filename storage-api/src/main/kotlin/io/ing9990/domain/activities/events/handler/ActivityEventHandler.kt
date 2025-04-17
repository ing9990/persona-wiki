package io.ing9990.domain.activities.events.handler

import io.ing9990.domain.activities.Activity
import io.ing9990.domain.activities.events.CreateActivityEvent
import io.ing9990.domain.activities.repositories.ActivityRepository
import io.ing9990.domain.user.User
import io.ing9990.domain.user.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ActivityEventHandler(
    private val activityRepository: ActivityRepository,
    private val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger(ActivityEventHandler::class.java)

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(phase = AFTER_COMMIT)
    fun handleCreateActivityEvent(event: CreateActivityEvent) {
        try {
            val activityEvent = event.activityEvent
            val user: User =
                userRepository.findById(activityEvent.userId).orElseThrow {
                    IllegalArgumentException("User with ID ${activityEvent.userId} not found")
                }

            val activity =
                Activity(
                    user = user,
                    activityType = activityEvent.activityType,
                    targetId = activityEvent.targetId,
                    targetName = activityEvent.targetName,
                    description = activityEvent.description,
                    prestigeAmount = activityEvent.activityType.prestigePoint,
                    categoryId = activityEvent.categoryId,
                    commentId = activityEvent.commentId,
                )

//            activity.activityType
//                .takeIf { it == LIKED }s
//                ?.let {
//                    val likedEvent: CommentLikedEvent = activityEvent as CommentLikedEvent
//                    // 좋아요 받은 인물
//                    val likedUser = likedEvent.likedUserId
//
//                    // 좋아요 한 인물
//                    val likeUser = likedEvent.likeUserId
//                }

            user.addPrestige(activity.activityType)
            activityRepository.save(activity)

            logger.info("Activity recorded: ${activityEvent.activityType} by user ${user.nickname}")
        } catch (e: Exception) {
            logger.error("Failed to record activity", e)
        }
    }
}
