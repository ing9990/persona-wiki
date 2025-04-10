package io.ing9990.googleimageapi

data class Queries(
    val request: List<QueryInfo>,
    val nextPage: List<QueryInfo>,
)
