package io.ing9990.common

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Redirect {
    companion object {
        /**
         * URL 인코딩을 적용한 리다이렉트 URL을 생성합니다.
         */
        fun to(
            categoryId: String,
            figureName: String,
        ): String {
            val encodedFigureName = URLEncoder.encode(figureName, StandardCharsets.UTF_8.name())
            val redirectUrl = "redirect:/$categoryId/@$encodedFigureName"

            return redirectUrl
        }
    }
}
