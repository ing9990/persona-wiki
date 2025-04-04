package io.ing9990.exceptions

class UnauthorizedException(
    val bindMessage: String,
) : RuntimeException(bindMessage) {
    constructor() : this("UnauthorizedException")
}
