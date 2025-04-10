package io.ing9990.exceptions

open class BussinessException(
    val errorCode: ErrorCode,
) : RuntimeException()
