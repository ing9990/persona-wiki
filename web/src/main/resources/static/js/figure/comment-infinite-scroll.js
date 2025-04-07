document.addEventListener("DOMContentLoaded", () => {
  const commentContainer = document.getElementById("comment-list");
  if (!commentContainer) {
    console.error("❌ #comment-list 요소가 없습니다.");
    return;
  }

  const figureId = commentContainer.dataset.figureId;
  if (!figureId) {
    console.error("❌ data-figure-id 속성이 비어있습니다.");
    return;
  }

  let page = 0;
  const size = 10;
  let loading = false;
  let lastPage = false;

  const fetchComments = async () => {
    if (loading || lastPage) {
      return;
    }
    loading = true;

    try {
      const response = await fetch(
          `/api/v1/${figureId}/comments?page=${page}&size=${size}`);
      if (!response.ok) {
        throw new Error("네트워크 에러");
      }

      const data = await response.json();

      data.content.forEach(comment => {
        const commentEl = document.createElement("div");
        commentEl.className = "comment";
        commentEl.innerHTML = `
          <div class="comment-header">
            <img src="${comment.userProfileImage || "/img/placeholder.svg"}" class="comment-profile" alt="프로필 이미지">
            <div class="comment-meta">
              <span class="comment-nickname">${escapeHtml(
            comment.userNickname || "익명")}</span>
              <span class="comment-date">${escapeHtml(comment.createdAt)}</span>
            </div>
          </div>
          <div class="comment-body">${escapeHtml(comment.content)}</div>
          <div class="comment-footer">
            <span class="like-btn" data-id="${comment.id}"><i class="fa-regular fa-thumbs-up"></i> <span class="count">${comment.likes}</span></span>
            <span class="dislike-btn" data-id="${comment.id}"><i class="fa-regular fa-thumbs-down"></i> <span class="count">${comment.dislikes}</span></span>
            <span class="reply-toggle"><i class="fa-regular fa-comment-dots"></i> 답글 ${comment.replyCount}</span>
          </div>
          <div class="reply-container mt-3 pl-3 border-l-2 border-gray-200 hidden">
            <div class="replies-list"></div>
            <div class="reply-form mt-2 hidden"></div>
          </div>
        `;
        commentContainer.appendChild(commentEl);
      });

      lastPage = data.last;
      page++;
    } catch (err) {
      console.error("댓글 로딩 실패:", err);
    } finally {
      loading = false;
    }
  };

  window.addEventListener("scroll", () => {
    const {scrollTop, scrollHeight, clientHeight} = document.documentElement;
    if (scrollTop + clientHeight >= scrollHeight - 100) {
      fetchComments();
    }
  });

  const escapeHtml = (unsafe) =>
      unsafe?.replace(/[&<"']/g, m => ({
        '&': "&amp;",
        '<': "&lt;",
        '"': "&quot;",
        "'": "&#039;"
      }[m])) || "";

  fetchComments();

  commentContainer.addEventListener("click", async (e) => {
    const target = e.target.closest(".like-btn, .dislike-btn");
    if (!target) {
      return;
    }

    const commentId = target.dataset.id;
    const countEl = target.querySelector(".count");
    const isLiked = target.classList.contains("clicked");
    const currentCount = parseInt(countEl.textContent, 10);

    try {
      const res = await fetch(`/api/v1/comments/${commentId}/toggle`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
      });

      if (!res.ok) {
        throw new Error("요청 실패");
      }

      // toggle 방식이므로 클릭 상태에 따라 증가 또는 감소
      if (isLiked) {
        target.classList.remove("clicked");
        countEl.textContent = currentCount - 1;
      } else {
        target.classList.add("clicked");
        countEl.textContent = currentCount + 1;
      }
    } catch (err) {
      console.error("좋아요 처리 실패:", err);
      alert("좋아요 처리 중 문제가 발생했습니다.");
    }
  });
});