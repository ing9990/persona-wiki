package io.ing9990.googleimageapi

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ImageSearchService(
    private val googleSearchClient: GoogleSearchClient,
) {
    @Value("\${google.search.api.key}")
    private lateinit var apiKey: String

    @Value("\${google.search.cx}")
    private lateinit var cx: String

    /**
     * 인물 이름으로 이미지를 검색합니다.
     * @param name 검색할 인물 이름
     * @return 찾은 이미지 URL, 없으면 null
     */
    fun searchImageByName(name: String): String? {
        val query = "$name 사진"

        return try {
            val response =
                googleSearchClient.searchImages(
                    apiKey = apiKey,
                    cx = cx,
                    query = query,
                    searchType = "image",
                )

            if (response.items.isNotEmpty()) {
                response.items[0].link
            } else {
                null
            }
        } catch (e: Exception) {
            // 에러 로깅
            println("이미지 검색 중 오류 발생: ${e.message}")
            null
        }
    }
}
