package io.ing9990.aop

/**
 * 인증된 사용자는 유저 엔티티를 반환하고 인증되지 않은 사용자는 null을 반환합니다.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrentUser
