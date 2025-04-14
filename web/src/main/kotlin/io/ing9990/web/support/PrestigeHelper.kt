package io.ing9990.web.support

import org.springframework.stereotype.Component

@Component
class PrestigeHelper {
    /**
     * 피보나치 수열에 따른 다음 레벨 요구 명성 포인트 계산
     * 0레벨: 100
     * 1레벨: 100
     * 2레벨: 200
     * 3레벨: 300
     * 4레벨: 500
     * 이후는 피보나치 수열을 따름
     */
    fun getPointsForNextLevel(prestige: Int): Int {
        val currentLevel = prestige / 100
        val nextLevel = currentLevel + 1
        val currentLevelPoints = prestige % 100

        val nextLevelRequirement =
            when (nextLevel) {
                1 -> 100
                2 -> 200
                3 -> 300
                4 -> 500
                else -> calculateFibonacciRequirement(nextLevel)
            }

        return nextLevelRequirement - currentLevelPoints
    }

    /**
     * 현재 레벨에서의 진행도 계산 (0-100%)
     */
    fun getProgressPercentage(prestige: Int): Int {
        val currentLevel = prestige / 100
        val currentLevelPoints = prestige % 100

        val nextLevelRequirement =
            when (currentLevel + 1) {
                1 -> 100
                2 -> 200
                3 -> 300
                4 -> 500
                else -> calculateFibonacciRequirement(currentLevel + 1)
            }

        return (currentLevelPoints * 100) / nextLevelRequirement
    }

    /**
     * 레벨에 따른 별명 반환
     */
    fun getLevelTitle(level: Int): String =
        when (level) {
            0 -> "병아리"
            1 -> "견습생"
            2 -> "관찰자"
            3 -> "평론가"
            4 -> "분석가"
            5 -> "영향력자"
            6 -> "전문가"
            7 -> "현자"
            8 -> "대가"
            9 -> "거장"
            10 -> "인물의 신"
            else -> if (level > 10) "인물의 신" else "병아리"
        }

    /**
     * 피보나치 수열 기반 레벨 요구 포인트 계산
     */
    private fun calculateFibonacciRequirement(level: Int): Int {
        if (level <= 0) return 0
        if (level == 1) return 100
        if (level == 2) return 200
        if (level == 3) return 300
        if (level == 4) return 500

        // 5레벨 이상은 피보나치로 계산
        var a = 300 // 3레벨 요구치
        var b = 500 // 4레벨 요구치
        var result = 0

        for (i in 5..level) {
            result = a + b
            a = b
            b = result
        }

        return result
    }
}
