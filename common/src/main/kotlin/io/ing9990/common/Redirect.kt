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
            slug: String,
        ): String {
            val encodedSlug = URLEncoder.encode(slug, StandardCharsets.UTF_8.name())
            val redirectUrl = "redirect:/$categoryId/@$encodedSlug"

            return redirectUrl
        }
    }
}
