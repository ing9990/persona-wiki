package io.ing9990.domain.user.service

import io.ing9990.domain.user.OAuthProviderType
import io.ing9990.domain.user.User
import io.ing9990.domain.user.repositories.querydsl.RankingRepository
import io.ing9990.domain.user.service.dto.RankingPageResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RankingService(
    private val rankingRepository: RankingRepository,
) {
    /**
     * 랭킹 페이지에 필요한 모든 데이터를 한번에 조회합니다.
     *
     * @param limit 가져올 최대 랭킹 수
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 랭킹 페이지에 필요한 모든 데이터를 포함한 결과 객체
     */
    @Transactional(readOnly = true)
    fun rankingTopLimit(
        limit: Int,
        page: Int,
        size: Int,
    ): RankingPageResult {
        // 유효성 검사
        val validLimit = if (limit <= 0) 100 else limit
        val validPage = if (page < 0) 0 else page
        val validSize = if (size <= 0) 20 else size

        // QueryDSL을 통해 랭킹 페이지 데이터 조회
        return rankingRepository.getRankingPageData(
            page = validPage,
            size = validSize,
            limit = validLimit,
        )
    }

    /**
     * 전체 사용자 수를 조회합니다.
     */
    @Transactional(readOnly = true)
    fun getUserCount(): Long = rankingRepository.countAllUsers()

    /**
     * 특정 사용자의 랭킹 순위를 조회합니다.
     */
    @Transactional(readOnly = true)
    fun getUserRank(userId: Long): Int = rankingRepository.getUserRank(userId)

    /**
     * 사용자 ID로 사용자를 찾습니다.
     */
    @Transactional(readOnly = true)
    fun findUserById(id: Long): User? = rankingRepository.findById(id).orElse(null)

    /**
     * 소셜 로그인 ID와 제공자로 사용자를 찾습니다.
     */
    @Transactional(readOnly = true)
    fun findByProviderIdAndProvider(
        providerId: String,
        provider: OAuthProviderType,
    ): User? = null // userRepository 필드에서 메서드 호출로 변경해야 함

    /**
     * 사용자 정보를 저장합니다.
     */
    @Transactional
    fun save(user: User): User = rankingRepository.save(user)
}
