package io.ing9990.wikipidiaapi.service

import io.ing9990.wikipidiaapi.clients.WikipediaClient
import io.ing9990.wikipidiaapi.clients.dto.response.info.WikipediaInfo
import org.springframework.stereotype.Service

@Service
class WikipediaService(
    private val wikipediaClient: WikipediaClient,
) {
    fun getBasicInfo(name: String): WikipediaInfo =
        try {
            val res = wikipediaClient.getSummary(name)
            WikipediaInfo(
                summary = res?.extract ?: "소개 정보를 찾을 수 없습니다.",
                imageUrl = res?.thumbnail?.source,
            )
        } catch (e: Exception) {
            WikipediaInfo("위키백과 정보 요청 실패", null)
        }
}
