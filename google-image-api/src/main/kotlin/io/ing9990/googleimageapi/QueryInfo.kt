package io.ing9990.googleimageapi

data class QueryInfo(
    val title: String,
    val totalResults: String,
    val searchTerms: String,
    val count: Int,
    val startIndex: Int,
    val inputEncoding: String,
    val outputEncoding: String,
    val safe: String,
    val cx: String,
    val searchType: String,
)
