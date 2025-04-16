document.addEventListener("DOMContentLoaded", function () {
  const commentContainer = document.getElementById("comment-list");
  const commentSection = document.getElementById("comment-section");

  if (!commentContainer || !commentSection) {
    return;
  }

  const figureId = commentContainer.dataset.figureId;
  if (!figureId) {
    console.error("figureId가 설정되어 있지 않습니다.");
    return;
  }

  const loadingIndicator = document.querySelector(".js-loading-indicator");
  const noMoreComments = document.querySelector(".js-no-more-comments");
  const errorMessage = document.querySelector(".js-error-message");
  const emptyMessage = document.querySelector(".js-no-comments");

  let page = 0;
  const size = 10;
  let isLoading = false;
  let isLastPage = false;

  loadComments();

  window.addEventListener("scroll", () => {
    if (
        isLoading ||
        isLastPage ||
        window.innerHeight + window.scrollY < document.body.offsetHeight - 300
    ) {
      return;
    }
    loadComments();
  });

  function showElement(el) {
    if (el && el.classList.contains("hidden")) {
      el.classList.remove("hidden");
    }
  }

  function hideElement(el) {
    if (el && !el.classList.contains("hidden")) {
      el.classList.add("hidden");
    }
  }

  async function loadComments() {
    isLoading = true;
    hideElement(errorMessage);
    showElement(loadingIndicator);

    try {
      const response = await fetch(
          `/figures/${figureId}/comments/fragment?page=${page}&size=${size}`
      );

      if (!response.ok) {
        throw new Error("서버 응답 오류");
      }

      const html = await response.text();
      console.log("서버에서 받은 댓글 fragment:", html);

      const tempDiv = document.createElement("div");
      tempDiv.innerHTML = html;

      // 1. 데이터가 비어있는 경우
      const children = Array.from(tempDiv.childNodes).filter(
          (node) => !(node.nodeType === Node.TEXT_NODE
              && !node.textContent.trim())
      );

      if (children.length === 0) {
        if (page === 0) {
          showElement(emptyMessage);
        }
        isLastPage = true;
        hideElement(loadingIndicator);
        showElement(noMoreComments);
        return;
      }

      // 2. last 여부를 fragment 루트에서 읽어오기
      const commentListFragment = tempDiv.querySelector("[data-last]");
      if (commentListFragment && commentListFragment.dataset.last === "true") {
        isLastPage = true;
        showElement(noMoreComments);
      }

      children.forEach((node) => {
        commentContainer.appendChild(node);
      });

      page += 1;
    } catch (err) {
      console.error("댓글을 불러오는 중 오류 발생:", err);
      showElement(errorMessage);
    } finally {
      isLoading = false;
      hideElement(loadingIndicator);
    }

    RelativeTimeUtils.updateAllRelativeTimes(".comment-date-relative")
  }
});

