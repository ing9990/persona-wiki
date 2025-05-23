package io.ing9990.web.support

import io.ing9990.common.To
import io.ing9990.domain.user.User
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class LayoutHelper {
    fun toSlug(name: String) = To.slug(name)

    fun toProfileLink(name: String) = "/users/$name"

    /**
     * 404 에러 페이지에 표시할 메시지를 생성합니다.
     */
    fun getErrorMessage(
        categoryId: String?,
        categoryName: String?,
        slug: String?,
    ): String =
        when {
            categoryId != null && slug != null -> "$categoryName 카테고리에서 '$slug'라는 인물을 찾을 수 없어요"
            categoryId != null -> "$categoryName 카테고리에서 인물을 찾을 수 없어요"
            slug != null -> "'$slug'라는 인물을 찾을 수 없어요"
            else -> "찾으시는 페이지가 존재하지 않아요"
        }

    /**
     * 카테고리 버튼에 표시할 텍스트를 생성합니다.
     */
    fun getCategoryButtonText(categoryName: String?): String =
        if (categoryName.isNullOrBlank()) {
            "카테고리 더보기"
        } else {
            "$categoryName 더보기"
        }

    val placeHolderAddress: String = "/img/profile-placeholder.svg"
    val categoryPlaceHolderAddress: String = "/img/category-placeholder.jpg"

    /**
     * 프로필 이미지를 가져옵니다.
     * - 유저 객체가 없거나 프로필 이미지가 null/빈문자열이면 placeholder 경로를 반환
     * - 그렇지 않으면 해당 프로필 이미지 경로를 반환
     */
    fun getProfileImage(userObj: Any?): String = (userObj as? User)?.image?.takeIf { it.isNotBlank() } ?: placeHolderAddress

    /**
     * 이미지 주소가 NULL 이라면 기본 이미지를 반환합니다.
     */
    fun getProfileImageByString(imageUrl: String?): String = imageUrl ?: placeHolderAddress

    /**
     * 카테고리 주소가 NULL 이라면 기본 이미지를 반환합니다.
     */
    fun getCategoryImageByString(imageUrl: String?): String = imageUrl ?: placeHolderAddress

    /**
     * 회원 상태: True
     * 비회원 상태: False
     */
    fun isLoggedIn(userObj: Any?): Boolean = userObj != null

    /**
     * LocalDateTime을 상대적 시간으로 변환합니다.
     */
    fun toRelative(dateTime: LocalDateTime): String {
        val now = LocalDateTime.now()
        val seconds = ChronoUnit.SECONDS.between(dateTime, now)
        val result =
            when {
                seconds < 30 -> "방금"
                seconds < 60 -> "${seconds}초 전"
                seconds < 3600 -> "${seconds / 60}분 전"
                seconds < 86400 -> "${seconds / 3600}시간 전"
                seconds < 604800 -> "${seconds / 86400}일 전"
                seconds < 2592000 -> "${seconds / 604800}주 전"
                seconds < 31536000 -> "${seconds / 2592000}달 전"
                else -> "${seconds / 31536000}년 전"
            }
        return result
    }

    /**
     * 사용자 프로필 링크를 가져옵니다.
     * - 유저 객체가 없으면 # 반환
     * - 그렇지 않으면 /users/{nickname} 경로 반환
     */
    fun getUserProfileLink(userObj: Any?): String = (userObj as? User)?.nickname?.let { "/users/$it" } ?: "#"

    /**
     * 인물 상세 페이지 URL을 생성합니다.
     */
    fun toDetail(
        categoryId: String,
        figureName: String,
    ): String = "/$categoryId/@${To.slug(figureName)}"
}
