package io.ing9990.googleimageapi

import io.ing9990.googleimageapi.QueryInfo

data class Queries(
    val request: List<QueryInfo>,
    val nextPage: List<QueryInfo>,
)
