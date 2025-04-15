package io.ing9990.domain.user

import io.ing9990.domain.activities.ActivityType
import io.ing9990.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

/**
 * 사용자 정보를 저장하는 엔티티
 *
 * @SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE user_id = ?")
 * @SQLRestriction("deleted_at is NULL")
 */
@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_prestige", columnList = "prestige"),
        Index(name = "idx_active_prestige", columnList = "prestige DESC"),
    ],
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,
    @Column(name = "provider_id", nullable = false, unique = true)
    var providerId: String? = null,
    @Column(name = "bio", nullable = false)
    var bio: String = "",
    @Enumerated(STRING)
    @Column(name = "oauth_provider", nullable = false)
    var provider: OAuthProviderType? = null,
    @Column(name = "nickname", nullable = false, unique = true, length = 40)
    var nickname: String,
    @Column(name = "profile_image", nullable = true)
    var image: String = "",
    @Column(name = "prestige")
    var prestige: Int = 0,
    @Column(name = "last_login_at", nullable = false)
    var lastLoginAt: LocalDateTime = LocalDateTime.now(),
) : BaseEntity() {
    companion object {
        fun create(
            providerId: String,
            provider: OAuthProviderType,
            image: String,
            nickname: String,
            lastLoginAt: LocalDateTime?,
        ): User =
            User(
                providerId = providerId,
                provider = provider,
                image = image,
                nickname = nickname,
                lastLoginAt = LocalDateTime.now(),
            )

        val regex = "^[a-zA-Z0-9가-힣]+$".toRegex()
    }

    fun addPrestige(type: ActivityType) {
        prestige += type.prestigePoint
    }

    fun deductPrestige(type: ActivityType) {
        prestige.minus(type.prestigePoint)
    }

    fun removeProfileImage() {
        this.image = ""
    }

    fun updateBio(updatedBio: String) {
        this.bio = updatedBio
    }
}
