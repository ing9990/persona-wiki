package io.ing9990.domain.user

import io.ing9990.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * 사용자 정보를 저장하는 엔티티
 *
 * @SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE user_id = ?")
 * @SQLRestriction("deleted_at is NULL")
 */
@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,
    @Column(name = "provider_id", nullable = false, unique = true)
    var providerId: String? = null,
    @Enumerated(STRING)
    @Column(name = "oauth_provider", nullable = false)
    var provider: OAuthProviderType? = null,
    @Column(name = "nickname", nullable = false, unique = true, length = 40)
    var nickname: String,
    @Column(name = "profile_image", nullable = true)
    var image: String = "",
    @Column(name = "last_login_at", nullable = false)
    var lastLoginAt: LocalDateTime = LocalDateTime.now(),
) : BaseEntity() {
    companion object {
        val regex = "^[a-zA-Z0-9가-힣]+$".toRegex()
    }

    fun removeProfileImage() {
        this.image = ""
    }
}
