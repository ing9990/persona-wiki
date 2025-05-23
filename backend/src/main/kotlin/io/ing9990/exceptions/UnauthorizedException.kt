package io.ing9990.exceptions

class UnauthorizedException(
    errorCode: ErrorCode,
) : BussinessException(errorCode)
