package io.ing9990.domain.figure.repository.querydsl

import io.ing9990.domain.figure.Comment

interface CommentCustomRepository {
    /**
     * 댓글 ID로 댓글과 모든 답글을 함께 조회합니다.
     *
     * @param commentId 조회할 댓글 ID
     * @return 답글이 초기화된 Comment 객체, 없으면 null
     */
    fun findWithRepliesById(commentId: Long): Comment?
}
