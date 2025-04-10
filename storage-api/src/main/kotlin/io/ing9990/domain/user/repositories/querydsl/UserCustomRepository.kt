package io.ing9990.domain.user.repositories.querydsl

import io.ing9990.domain.user.service.dto.UserResult

interface UserCustomRepository {
    fun getRecentActivities(
        userId: Long,
        limit: Int,
    ): List<UserResult>
}
