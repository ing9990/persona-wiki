package io.ing9990.googleimageapi

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "googleSearch", url = "https://www.googleapis.com/customsearch/v1")
interface GoogleSearchClient {
    @GetMapping
    fun searchImages(
        @RequestParam("key") apiKey: String,
        @RequestParam("cx") cx: String,
        @RequestParam("q") query: String,
        @RequestParam("searchType") searchType: String,
        @RequestParam("num") num: Int = 1,
    ): GoogleSearchResponse
}
