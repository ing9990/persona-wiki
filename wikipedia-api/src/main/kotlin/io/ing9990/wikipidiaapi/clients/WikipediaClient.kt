package io.ing9990.wikipidiaapi.clients

import WikipediaSummaryResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "wikipediaClient", url = "https://ko.wikipedia.org/api/rest_v1")
interface WikipediaClient {
    @GetMapping("/page/summary/{name}")
    fun getSummary(
        @PathVariable("name") name: String?,
    ): WikipediaSummaryResponse?
}
