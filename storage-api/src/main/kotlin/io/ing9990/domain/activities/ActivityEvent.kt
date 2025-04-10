package io.ing9990.domain.activities

import java.time.LocalDateTime

abstract class ActivityEvent(
    open val userId: Long,
    open val activityType: ActivityType,
    open val targetId: Long,
    open val targetName: String,
    open val description: String? = null,
    open val timestamp: LocalDateTime = LocalDateTime.now(),
)
