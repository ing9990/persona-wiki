package io.ing9990.domain

/**
 * 엔티티를 찾을 수 없을 때 발생하는 예외
 */
class EntityNotFoundException(
    val entityType: String,
    val identifier: Any,
    message: String = "$entityType with identifier $identifier not found",
) : RuntimeException(message)
