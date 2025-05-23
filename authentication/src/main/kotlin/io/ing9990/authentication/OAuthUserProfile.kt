package io.ing9990.authentication

interface OAuthUserProfile {
    fun findSocialId(): String

    fun findUsername(): String

    fun findImageUrl(): String

    fun findEmail(): String?
}
