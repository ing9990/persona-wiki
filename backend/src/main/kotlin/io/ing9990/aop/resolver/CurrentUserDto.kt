package io.ing9990.aop.resolver

import io.ing9990.aop.resolver.LoginStatus.LOGGED_IN
import io.ing9990.domain.user.User

data class CurrentUserDto(
    val currentUser: User?,
    val status: LoginStatus,
) {
    companion object {
        const val DEFAULT: Long = -1
        const val DEFAULT_IMAGE: String = "/img/profile-placeholder.svg"
    }

    fun loggedIn(): Boolean = status == LOGGED_IN

    fun notLoggedIn(): Boolean = status != LOGGED_IN

    fun image(): String = currentUser?.image ?: ""

    fun getUserIdOrDefault(): Long = currentUser?.id ?: DEFAULT

    fun toProfile(): String = "/users/${currentUser?.nickname}"
}
