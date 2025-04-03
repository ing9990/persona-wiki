package io.ing9990.web.support

import io.ing9990.domain.user.User
import org.springframework.stereotype.Service

@Service
class LayoutHelper {
    val placeHolderAddress: String = "/img/profile-placeholder.png"

    /**
     * 프로필 이미지를 가져옵니다.
     * - 유저 객체가 없거나 프로필 이미지가 null/빈문자열이면 placeholder 경로를 반환
     * - 그렇지 않으면 해당 프로필 이미지 경로를 반환
     */
    fun getProfileImage(userObj: Any?): String =
        (userObj as? User)
            ?.profileImage
            ?.takeIf { it.isNotBlank() }
            ?: placeHolderAddress

    /**
     * 회원 상태: True
     * 비회원 상태: False
     */
    fun isLoggedIn(userObj: Any?): Boolean = userObj != null
}
