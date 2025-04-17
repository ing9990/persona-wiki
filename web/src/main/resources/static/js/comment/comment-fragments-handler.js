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

  window.page = 0;
  const size = 10;
  window.isLoading = false;
  window.isLastPage = false;

  // URL에서 특정 댓글 ID를 체크 (예: #comment-123)
  const hash = window.location.hash;
  window.targetCommentId = null;
  window.autoExpandReplies = false; // 자동 펼침 제어 플래그

  if (hash && hash.startsWith('#comment-')) {
    window.targetCommentId = hash.substring(9); // '#comment-' 이후의 문자열
    window.autoExpandReplies = true; // 해시가 있을 때만 자동 펼침 활성화

    // 로딩 표시 활성화
    if (loadingIndicator) {
      loadingIndicator.classList.remove("hidden");
    }
  }

  // 페이지 로딩 시 첫 페이지 로드
  loadComments().then(() => {
    // 특정 댓글로 이동해야 하는 경우, 해당 댓글이 이미 로드되었는지 확인
    if (window.targetCommentId) {
      const targetComment = document.getElementById(
          'comment-' + window.targetCommentId);
      if (targetComment) {
        // 이미 로드된 경우 해당 댓글로 스크롤
        highlightAndScrollToComment(targetComment);
        window.targetCommentId = null;
        window.autoExpandReplies = false;
      } else if (!window.isLastPage) {
        // 아직 로드되지 않은 경우 더 많은 댓글 로드
        loadUntilCommentFound(window.targetCommentId);
      } else {
        // 모든 댓글을 로드했지만 찾지 못한 경우
        console.log("모든 댓글을 로드했지만 타겟 댓글을 찾지 못했습니다:", window.targetCommentId);
        hideElement(loadingIndicator);
        window.autoExpandReplies = false;
      }
    }
  });

  // 스크롤 이벤트 리스너
  window.addEventListener("scroll", () => {
    if (
        window.isLoading ||
        window.isLastPage ||
        window.innerHeight + window.scrollY < document.body.offsetHeight - 300
        ||
        window.targetCommentId // 타겟 댓글 찾는 중이면 스크롤 이벤트 무시
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

  // 댓글을 찾을 때까지 추가 로드하는 함수
  async function loadUntilCommentFound(commentId) {
    const maxAttempts = 10; // 최대 시도 횟수
    let attempts = 0;

    while (!window.isLastPage && attempts < maxAttempts) {
      attempts++;
      await loadComments();

      // 댓글을 찾았는지 확인
      const targetComment = document.getElementById('comment-' + commentId);
      if (targetComment) {
        highlightAndScrollToComment(targetComment);
        window.targetCommentId = null;
        window.autoExpandReplies = false;
        return true;
      }

      // 약간의 지연 추가
      await new Promise(resolve => setTimeout(resolve, 300));
    }

    console.log(`댓글 ID ${commentId}를 ${attempts}번 시도했으나 찾지 못했습니다.`);
    hideElement(loadingIndicator);
    window.autoExpandReplies = false;
    return false;
  }

  // 댓글 로드 함수 - Promise 반환하도록 수정
  async function loadComments() {
    window.isLoading = true;
    hideElement(errorMessage);
    showElement(loadingIndicator);

    try {
      const response = await fetch(
          `/figures/${figureId}/comments/fragment?page=${window.page}&size=${size}`
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
        if (window.page === 0) {
          showElement(emptyMessage);
        }
        window.isLastPage = true;
        hideElement(loadingIndicator);
        showElement(noMoreComments);
        return false;
      }

      // 2. last 여부를 fragment 루트에서 읽어오기
      const commentListFragment = tempDiv.querySelector("[data-last]");
      if (commentListFragment && commentListFragment.dataset.last === "true") {
        window.isLastPage = true;
        showElement(noMoreComments);
      }

      children.forEach((node) => {
        commentContainer.appendChild(node);
      });

      window.page += 1;

      // 3. 댓글이 추가된 후 타겟 댓글이 있는지 확인
      if (window.targetCommentId) {
        const targetComment = document.getElementById(
            'comment-' + window.targetCommentId);
        if (targetComment) {
          // 타겟 댓글을 찾았으면 스크롤
          highlightAndScrollToComment(targetComment);
          window.targetCommentId = null; // 찾았으므로 타겟 제거
          window.autoExpandReplies = false; // 자동 펼침 비활성화
        }
      }

      return true;
    } catch (err) {
      console.error("댓글을 불러오는 중 오류 발생:", err);
      showElement(errorMessage);
      return false;
    } finally {
      window.isLoading = false;
      if (!window.targetCommentId) {
        hideElement(loadingIndicator);
      }
      RelativeTimeUtils.updateAllRelativeTimes(".comment-date-relative");
    }
  }

  // 댓글을 하이라이트하고 스크롤하는 함수
  function highlightAndScrollToComment(element) {
    hideElement(loadingIndicator);

    // 스크롤 및 하이라이트 효과 (약간의 지연 후)
    setTimeout(() => {
      element.scrollIntoView({behavior: 'smooth', block: 'center'});
      element.style.transition = 'background-color 0.5s ease';
      element.style.backgroundColor = 'rgba(250, 240, 137, 0.5)';

      // 3초 후 하이라이트 제거
      setTimeout(() => {
        element.style.backgroundColor = '';
      }, 3000);

      // 답글 컨테이너가 있고 해당 댓글에 답글이 있는 경우 자동으로 펼치기
      if (window.autoExpandReplies) {
        const viewRepliesBtn = element.querySelector('.view-replies-btn');
        if (viewRepliesBtn) {
          viewRepliesBtn.click();
        }
      }
    }, 200);
  }
});