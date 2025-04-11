package io.ing9990.common

class To {
    companion object {
        // FigureName을 Slug로 변경합니다.
        fun slug(figureName: String) =
            figureName
                .trim()
                .substringBefore("_")
                .replace(" ", "")
    }
}
