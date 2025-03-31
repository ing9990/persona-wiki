package io.ing9990.domain.figure

/**
 * 댓글 유형을 나타내는 Enum 클래스
 * ROOT: 원 댓글 (최상위 댓글)
 * REPLY: 답글 (다른 댓글에 대한 응답)
 */
enum class CommentType {
    ROOT,    // 원 댓글
    REPLY    // 답글
}