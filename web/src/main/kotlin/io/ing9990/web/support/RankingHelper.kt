package io.ing9990.web.support

import org.springframework.stereotype.Service

/**
 * 랭킹 화면에서 사용되는 유틸리티 함수들을 모아놓은 헬퍼 클래스
 */
@Service
class RankingHelper {
    /**
     * 레벨별 별명
     */
    private val levelTitles =
        listOf(
            "병아리",
            "견습생",
            "관찰자",
            "평론가",
            "분석가",
            "영향력자",
            "전문가",
            "현자",
            "대가",
            "거장",
            "인물의 신",
        )

    /**
     * 레벨에 따른 별명 반환
     */
    fun getLevelTitle(level: Int): String =
        when {
            level < 0 -> levelTitles.first()
            level >= levelTitles.size -> levelTitles.last()
            else -> levelTitles[level]
        }

    /**
     * 피보나치 수열 기반 레벨 요구 점수 계산
     */
    private fun calculateLevelRequirement(level: Int): Int =
        when {
            level <= 0 -> 0
            level == 1 -> 100
            level == 2 -> 200
            level == 3 -> 300
            level == 4 -> 500
            else -> {
                // 5레벨 이상은 피보나치로 계산
                var a = 300
                var b = 500
                var result = 0

                for (i in 5..level) {
                    result = a + b
                    a = b
                    b = result
                }

                result
            }
        }

    /**
     * 사용자 레벨 및 진행도 계산
     */
    fun calculateLevel(prestige: Int): LevelInfo {
        var level = 0
        var accumulatedPoints = 0
        var nextLevelRequirement = calculateLevelRequirement(level + 1)

        while (prestige >= accumulatedPoints + nextLevelRequirement) {
            accumulatedPoints += nextLevelRequirement
            level++
            nextLevelRequirement = calculateLevelRequirement(level + 1)
        }

        // 다음 레벨까지 남은 포인트
        val pointsForNextLevel = nextLevelRequirement
        val currentLevelPoints = prestige - accumulatedPoints
        val progressPercentage =
            ((currentLevelPoints.toDouble() / pointsForNextLevel) * 100).toInt()

        return LevelInfo(
            level = level,
            currentLevelPoints = currentLevelPoints,
            pointsForNextLevel = pointsForNextLevel,
            progressPercentage = progressPercentage,
            totalPrestige = prestige,
            title = getLevelTitle(level),
        )
    }

    /**
     * 레벨 정보를 담는 데이터 클래스
     */
    data class LevelInfo(
        val level: Int,
        val currentLevelPoints: Int,
        val pointsForNextLevel: Int,
        val progressPercentage: Int,
        val totalPrestige: Int,
        val title: String,
    )
}
