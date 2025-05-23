package io.ing9990.common

class To {
    companion object {
        fun slug(name: String): String =
            name
                .trim()
                .lowercase()
                .replace(" ", "-") // 공백 → 하이픈
                .replace("_", "-") // 언더스코어 → 하이픈
                .replace(Regex("[^a-z0-9가-힣-]"), "")
    }
}
