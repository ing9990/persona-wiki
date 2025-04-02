package io.ing9990.web.support

import io.ing9990.domain.user.User
import org.springframework.stereotype.Service

@Service
class LayoutHelper {
    /**
     * 프로필 이미지를 가져옵니다.
     * - 유저 객체가 없거나 프로필 이미지가 null/빈문자열이면 placeholder 경로를 반환
     * - 그렇지 않으면 해당 프로필 이미지 경로를 반환
     */
    fun getProfileImage(userObj: Any?): String {
        if (userObj == null) {
            return "/img/profile-placeholder.png"
        }

        val user = userObj as? User ?: return "/img/profile-placeholder.png"
        val profileImage = user.profileImage

        return if (profileImage.isNullOrBlank()) "/img/profile-placeholder.png" else profileImage
    }

    /**
     * 로그인 여부를 판단하는 헬퍼 메서드
     */
    fun isLoggedIn(userObj: Any?): Boolean = userObj != null
}
