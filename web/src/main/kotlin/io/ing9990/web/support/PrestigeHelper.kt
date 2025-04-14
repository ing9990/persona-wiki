package io.ing9990.web.support

import org.springframework.stereotype.Component

@Component
class PrestigeHelper {
    /**
     * JS와 동일하게 누적 포인트 기준으로 다음 레벨까지 필요한 포인트 계산
     */
    fun getPointsForNextLevel(prestige: Int): Int {
        var level = 0
        var accumulatedPoints = 0
        var nextRequirement = calculateFibonacciRequirement(level + 1)

        while (prestige >= accumulatedPoints + nextRequirement) {
            accumulatedPoints += nextRequirement
            level++
            nextRequirement = calculateFibonacciRequirement(level + 1)
        }

        val currentLevelPoints = prestige - accumulatedPoints
        return nextRequirement - currentLevelPoints
    }

    /**
     * 현재 레벨에서의 진행도 계산 (0-100%)
     */
    fun getProgressPercentage(prestige: Int): Int {
        var level = 0
        var accumulatedPoints = 0
        var nextRequirement = calculateFibonacciRequirement(level + 1)

        while (prestige >= accumulatedPoints + nextRequirement) {
            accumulatedPoints += nextRequirement
            level++
            nextRequirement = calculateFibonacciRequirement(level + 1)
        }

        val currentLevelPoints = prestige - accumulatedPoints
        return (currentLevelPoints * 100) / nextRequirement
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
    private fun calculateFibonacciRequirement(level: Int): Int =
        when (level) {
            0 -> 0
            1 -> 100
            2 -> 200
            3 -> 300
            4 -> 500
            else -> {
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

    fun getLevel(prestige: Int): Int {
        var level = 0
        var accumulatedPoints = 0
        var nextRequirement = calculateFibonacciRequirement(level + 1)

        while (prestige >= accumulatedPoints + nextRequirement) {
            accumulatedPoints += nextRequirement
            level++
            nextRequirement = calculateFibonacciRequirement(level + 1)
        }

        return level
    }
}
