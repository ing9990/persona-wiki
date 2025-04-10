package io.ing9990.googleimageapi

data class GoogleSearchResponse(
    val kind: String,
    val url: UrlInfo,
    val queries: Queries,
    val context: Context,
    val searchInformation: SearchInformation,
    val items: List<SearchItem>,
)
