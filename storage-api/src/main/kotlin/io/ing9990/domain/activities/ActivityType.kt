package io.ing9990.domain.activities

enum class ActivityType(
    val prestigePoint: Int,
    val text: String,
) {
    PERSON_ADD(50, "인물 추가"),
    PERSON_EDIT(10, "인물 정보 수정"),
    COMMENT(5, "댓글 작성"),
    REPLY(3, "답글 작성"),
    VOTE(15, "투표 참여"),
    LIKE(1, "댓글 좋아요"),
    DISLIKE(1, "댓글 싫어요"),
    LIKED(5, "댓글 좋아요 받음"),
    DISLIKED(0, "댓글 싫어요 받음"),
}
