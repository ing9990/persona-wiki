package io.ing9990.common

class HangeulUtil {
    companion object {
        // 한글 초성 매핑
        val CHOSUNG_MAP =
            mapOf(
                'ㄱ' to Regex("^[가-깋]"),
                'ㄲ' to Regex("^[까-낗]"),
                'ㄴ' to Regex("^[나-닣]"),
                'ㄷ' to Regex("^[다-딯]"),
                'ㄸ' to Regex("^[따-띻]"),
                'ㄹ' to Regex("^[라-맇]"),
                'ㅁ' to Regex("^[마-밓]"),
                'ㅂ' to Regex("^[바-빟]"),
                'ㅃ' to Regex("^[빠-삫]"),
                'ㅅ' to Regex("^[사-싷]"),
                'ㅆ' to Regex("^[싸-앃]"),
                'ㅇ' to Regex("^[아-잏]"),
                'ㅈ' to Regex("^[자-짛]"),
                'ㅉ' to Regex("^[짜-찧]"),
                'ㅊ' to Regex("^[차-칳]"),
                'ㅋ' to Regex("^[카-킿]"),
                'ㅌ' to Regex("^[타-팋]"),
                'ㅍ' to Regex("^[파-핗]"),
                'ㅎ' to Regex("^[하-힣]"),
            )

        /**
         * 인물 이름이 주어진 초성 패턴과 일치하는지 확인
         */
        fun matchesWithChosung(
            name: String,
            chosungPattern: String,
        ): Boolean {
            // 이름에서 초성 추출
            val nameChosung = extractChosung(name)

            // 초성 패턴이 이름의 초성에 포함되는지 확인
            return nameChosung.startsWith(chosungPattern, ignoreCase = true) ||
                name.contains(chosungPattern, ignoreCase = true)
        }

        /**
         * 문자열에서 초성을 추출
         */
        private fun extractChosung(str: String): String {
            return str.map { char ->
                when {
                    char in 'ㄱ'..'ㅎ' -> char // 이미 초성인 경우
                    char in '가'..'힣' -> {
                        // 한글 유니코드 계산으로 초성 추출
                        val chosungIndex = (char.code - '가'.code) / 588
                        "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"[chosungIndex]
                    }

                    else -> char // 한글이 아닌 경우 그대로
                }
            }.joinToString("")
        }
    }
}
