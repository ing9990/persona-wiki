/**
 * 댓글 무한 스크롤 및 관리 모듈
 * 댓글 로드, 작성, 좋아요/싫어요, 답글 기능 처리
 */
document.addEventListener("DOMContentLoaded", () => {
  if (typeof CommentAPI === 'undefined') {
    console.warn('CommentAPI 모듈이 로드되지 않았습니다.');
  }
  // Toast 관리자 초기화
  initializeToastManager();

  // 댓글 모듈 정의
  const CommentModule = (() => {
    // 상수 정의
    const CONFIG = {
      SELECTORS: {
        COMMENT_CONTAINER: "#comment-list",
        EMPTY_MESSAGE: ".empty-comment-message",
        COMMENT_FORM: "form[action*='/comments']",
        LOADING_INDICATOR: ".loading-indicator",
        NO_MORE_COMMENTS: ".no-more-comments"
      },
      PAGE_SIZE: 10,
      ANIMATION: {
        DURATION: 300,
        EASE: "ease-in-out"
      }
    };

    // 상태 관리
    const state = {
      figureId: null,
      page: 0,
      loading: false,
      lastPage: false,
      isLoggedIn: false  // 로그인 상태를 저장
    };

    // 유틸리티 함수
    const utils = {
      escapeHtml: (unsafe) =>
          unsafe?.replace(/[&<"']/g, m => ({
            '&': "&amp;",
            '<': "&lt;",
            '"': "&quot;",
            "'": "&#039;"
          }[m])) || "",

      formatTimeAgo: (datetimeStr) => {
        const date = new Date(datetimeStr.replace(/-/g, "/"));
        const now = new Date();
        const diff = Math.floor((now - date) / 1000);

        if (diff < 10) {
          return "방금 전";
        }
        if (diff < 60) {
          return `${diff}초 전`;
        }
        if (diff < 3600) {
          return `${Math.floor(diff / 60)}분 전`;
        }
        if (diff < 86400) {
          return `${Math.floor(diff / 3600)}시간 전`;
        }
        if (diff < 2592000) {
          return `${Math.floor(diff / 86400)}일 전`;
        }
        return date.toLocaleDateString();
      },

      // 가장 확실한 방법으로 로그인 상태 확인
      checkLoginStatus: () => {
        // 1. 직접 DOM에서 로그인 상태 확인 (가장 확실한 방법)
        const loginElement = document.getElementById('userLoginStatus');
        if (loginElement && loginElement.value === 'true') {
          return true;
        }

        // 2. Thymeleaf 조건부 요소 확인
        if (document.querySelector('[data-logged-in="true"]')) {
          return true;
        }

        // 3. auth-manager.js 활용 (사용 가능한 경우)
        if (window.auth && typeof window.auth.isLoggedIn === 'function') {
          return window.auth.isLoggedIn();
        }

        // 모든 검사 실패 시 비로그인으로 간주
        return false;
      },

      // 엘리먼트에 페이드 인 애니메이션 적용
      fadeIn: (element, duration = CONFIG.ANIMATION.DURATION) => {
        if (!element) {
          return;
        }

        element.style.transition = `opacity ${duration}ms ${CONFIG.ANIMATION.EASE}`;
        element.style.opacity = '0';

        setTimeout(() => {
          element.style.opacity = '1';
        }, 10);
      },

      // 엘리먼트에 페이드 아웃 애니메이션 적용
      fadeOut: (element, duration = CONFIG.ANIMATION.DURATION) => {
        if (!element) {
          return;
        }

        element.style.transition = `opacity ${duration}ms ${CONFIG.ANIMATION.EASE}`;
        element.style.opacity = '1';

        setTimeout(() => {
          element.style.opacity = '0';
        }, 10);
      },

      // 부드럽게 스크롤 이동
      smoothScrollTo: (element, offset = 0) => {
        if (!element) {
          return;
        }

        const rect = element.getBoundingClientRect();
        const targetPosition = rect.top + window.pageYOffset + offset;

        window.scrollTo({
          top: targetPosition,
          behavior: 'smooth'
        });
      }
    };

    // DOM 요소 초기화 및 검증
    function initializeDOM() {
      const commentContainer = document.querySelector(
          CONFIG.SELECTORS.COMMENT_CONTAINER);
      if (!commentContainer) {
        console.error("❌ 댓글 컨테이너를 찾을 수 없습니다");
        return null;
      }

      state.figureId = commentContainer.dataset.figureId;
      if (!state.figureId) {
        console.error("❌ 인물 ID를 찾을 수 없습니다");
        return null;
      }

      // 로그인 상태 초기화
      state.isLoggedIn = utils.checkLoginStatus();
      console.log("[CommentModule] 로그인 상태:", state.isLoggedIn);

      return commentContainer;
    }

    // Toast 매니저 초기화
    function initializeToastManager() {
      if (typeof window.toast === 'undefined') {
        window.toast = {
          success: (msg) => alert('성공: ' + msg),
          error: (msg) => alert('오류: ' + msg),
          warning: (msg) => alert('경고: ' + msg),
          info: (msg) => alert('알림: ' + msg)
        };
        console.warn('Toast 매니저가 로드되지 않았습니다. alert으로 대체합니다.');
      }
    }

    // 좋아요/싫어요 버튼 처리
    async function handleLikeDislike(target, isLike, figureId, commentId) {
      // 로그인 상태 재확인
      if (!state.isLoggedIn) {
        window.toast.error("좋아요/싫어요 기능은 로그인 후 이용 가능합니다.");
        return;
      }

      const countEl = target.querySelector(".count");
      const isClicked = target.classList.contains("clicked");
      const currentCount = parseInt(countEl?.textContent || "0", 10);

      const siblingSelector = isLike ? ".dislike-btn" : ".like-btn";
      const opposite = target.closest(".comment-footer")?.querySelector(
          siblingSelector);
      const oppositeCountEl = opposite?.querySelector(".count");
      const oppositeIsClicked = opposite?.classList.contains("clicked");
      const oppositeCount = parseInt(oppositeCountEl?.textContent || "0", 10);

      try {
        // API 호출
        await CommentAPI.toggleLikeDislike(figureId, commentId);

        // 클릭 상태 토글
        if (isClicked) {
          target.classList.remove("clicked",
              isLike ? "text-blue-600" : "text-red-500");
          if (countEl) {
            countEl.textContent = currentCount - 1;
          }
        } else {
          target.classList.add("clicked",
              isLike ? "text-blue-600" : "text-red-500");
          if (countEl) {
            countEl.textContent = currentCount + 1;
          }

          // 반대쪽이 클릭되어 있었다면 해제
          if (oppositeIsClicked) {
            opposite.classList.remove("clicked",
                isLike ? "text-red-500" : "text-blue-600");
            if (oppositeCountEl) {
              oppositeCountEl.textContent = oppositeCount - 1;
            }
          }
        }
      } catch (err) {
        console.error("좋아요/싫어요 처리 실패:", err);
        window.toast.error("처리 중 문제가 발생했습니다.");
      }
    }

    // 댓글 요소 생성
    function createCommentElement(comment, figureId) {
      const el = document.createElement("div");
      el.className = "comment mb-6";

      // 답글 쓰기 버튼 (로그인 상태에 따라 다르게 표시)
      const replyWriteButton = state.isLoggedIn
          ? `<span class="reply-write-btn cursor-pointer text-gray-600 hover:text-blue-600 flex items-center gap-1" data-id="${comment.id}"><i class="fa-regular fa-pen-to-square"></i> 답글 쓰기</span>`
          : `<span class="reply-write-disabled text-gray-400 flex items-center gap-1" title="로그인 후 이용 가능합니다"><i class="fa-regular fa-pen-to-square"></i> 답글 쓰기</span>`;

      el.innerHTML = `
        <div class="comment-header flex items-center gap-3">
          <img src="${comment.userProfileImage || "/img/placeholder.svg"}" class="comment-profile w-8 h-8 rounded-full object-cover" alt="프로필 이미지" data-handle-error="true" data-placeholder="/img/profile-placeholder.svg">
          <div class="comment-meta">
            <span class="comment-nickname font-semibold">${utils.escapeHtml(
          comment.userNickname || "익명")}</span>
            <span class="comment-date ml-2 text-sm text-gray-400">${utils.formatTimeAgo(
          comment.createdAt)}</span>
          </div>
        </div>
        <div class="comment-body mt-2 text-gray-800 text-[15px] leading-relaxed">${utils.escapeHtml(
          comment.content)}</div>
        <div class="comment-footer flex flex-wrap gap-4 text-sm text-gray-500 mt-2 items-center">
          <span class="like-btn cursor-pointer ${comment.isLikedByUser
          ? 'clicked text-blue-600'
          : ''}" data-id="${comment.id}" data-figure-id="${figureId}">
            <i class="fa-regular fa-thumbs-up"></i> <span class="count">${comment.likes
      || 0}</span>
          </span>
          <span class="dislike-btn cursor-pointer ${comment.isDislikedByUser
          ? 'clicked text-red-500'
          : ''}" data-id="${comment.id}" data-figure-id="${figureId}">
            <i class="fa-regular fa-thumbs-down"></i> <span class="count">${comment.dislikes
      || 0}</span>
          </span>
          ${replyWriteButton}
          ${comment.replyCount > 0
          ? `<span class="reply-toggle cursor-pointer font-medium text-blue-600 hover:underline" data-comment-id="${comment.id}" data-figure-id="${figureId}">
            <i class="fa-regular fa-comment-dots"></i> 답글 ${comment.replyCount}개</span>`
          : ""}
        </div>
        <div class="reply-container mt-3 pl-6 ml-2 border-l-2 border-blue-100 hidden">
          <div class="replies-list"></div>
          ${state.isLoggedIn ? `
          <div class="reply-form mt-2 hidden">
            <textarea class="reply-textarea w-full border border-gray-300 rounded-md px-3 py-2 text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-400" rows="2" placeholder="답글을 입력하세요..."></textarea>
            <button class="submit-reply-btn mt-2 px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700">답글 작성</button>
          </div>
          ` : ''}
        </div>
      `;

      return el;
    }

    // 댓글 목록 가져오기
    async function fetchComments(commentContainer) {
      if (state.loading || state.lastPage) {
        return;
      }

      state.loading = true;

      // 로딩 인디케이터 표시
      const loadingIndicator = document.querySelector(
          CONFIG.SELECTORS.LOADING_INDICATOR);
      if (loadingIndicator) {
        loadingIndicator.classList.remove("hidden");
        utils.fadeIn(loadingIndicator);
      }

      try {
        // API 호출
        const data = await CommentAPI.fetchComments(
            state.figureId,
            state.page,
            CONFIG.PAGE_SIZE
        );

        const emptyMessageEl = document.querySelector(
            CONFIG.SELECTORS.EMPTY_MESSAGE);

        // 빈 메시지 표시 여부 결정
        if (state.page === 0 && data.content.length === 0) {
          if (emptyMessageEl) {
            emptyMessageEl.classList.remove("hidden");
            utils.fadeIn(emptyMessageEl);
          }
        } else {
          if (emptyMessageEl) {
            emptyMessageEl.classList.add("hidden");
          }
        }

        // 댓글 추가
        data.content.forEach(comment => {
          const commentElement = createCommentElement(comment, state.figureId);
          commentContainer.appendChild(commentElement);
          utils.fadeIn(commentElement);
        });

        // 마지막 페이지 설정
        state.lastPage = data.last;

        // 마지막 페이지에 도달했을 때 메시지 표시
        if (state.lastPage && state.page > 0 && data.content.length > 0) {
          const noMoreCommentsEl = document.querySelector(
              CONFIG.SELECTORS.NO_MORE_COMMENTS);
          if (noMoreCommentsEl) {
            noMoreCommentsEl.classList.remove("hidden");
            utils.fadeIn(noMoreCommentsEl);
          }
        }

        state.page++;
      } catch (err) {
        console.error("댓글 로딩 실패:", err);
        window.toast.error("댓글을 불러오는 중 오류가 발생했습니다.");
      } finally {
        state.loading = false;

        // 로딩 인디케이터 숨김
        if (loadingIndicator) {
          utils.fadeOut(loadingIndicator);
          setTimeout(() => {
            loadingIndicator.classList.add("hidden");
          }, CONFIG.ANIMATION.DURATION);
        }
      }
    }

    // 무한 스크롤 설정
    function setupInfiniteScroll(commentContainer) {
      const sentinel = document.createElement("div");
      sentinel.id = "scroll-sentinel";
      sentinel.className = "py-4";
      commentContainer.after(sentinel);

      const observer = new IntersectionObserver(entries => {
        if (entries[0].isIntersecting && !state.loading && !state.lastPage) {
          fetchComments(commentContainer);
        }
      }, {
        rootMargin: '100px', // 미리 로딩 시작
      });

      observer.observe(sentinel);
    }

    // 댓글 이벤트 처리 설정
    function setupCommentEvents(commentContainer) {
      // 댓글 목록 이벤트 처리 (이벤트 위임)
      commentContainer.addEventListener("click", async (e) => {
        const target = e.target.closest(
            ".like-btn, .dislike-btn, .reply-toggle, .reply-write-btn, .submit-reply-btn");
        if (!target) {
          return;
        }

        // 비로그인 사용자 처리
        if (!state.isLoggedIn && (target.classList.contains("like-btn")
            || target.classList.contains("dislike-btn"))) {
          window.toast.error("좋아요/싫어요 기능은 로그인 후 이용 가능합니다.");
          return;
        }

        const commentEl = target.closest(".comment");
        const commentId = target.dataset.id || target.dataset.commentId;

        // 좋아요/싫어요 버튼 처리
        if (target.classList.contains("like-btn") || target.classList.contains(
            "dislike-btn")) {
          const isLikeButton = target.classList.contains("like-btn");
          await handleLikeDislike(target, isLikeButton, state.figureId,
              commentId);
          return;
        }

        // 답글 토글 버튼 처리
        if (target.classList.contains("reply-toggle")) {
          await handleReplyToggle(commentEl, target);
          return;
        }

        // 답글 작성 버튼 처리
        if (target.classList.contains("reply-write-btn")) {
          const replyForm = commentEl.querySelector(".reply-form");
          replyForm.classList.toggle("hidden");

          // 폼이 표시될 때 textarea에 포커스
          if (!replyForm.classList.contains("hidden")) {
            replyForm.querySelector(".reply-textarea").focus();

            // 스크롤 이동
            utils.smoothScrollTo(replyForm, -100);
          }
          return;
        }

        // 답글 제출 버튼 처리
        if (target.classList.contains("submit-reply-btn")) {
          await handleReplySubmit(commentEl, target, commentId);
        }
      });

      // 비로그인 사용자가 비활성화된 답글 쓰기를 클릭한 경우
      commentContainer.addEventListener("click", (e) => {
        if (e.target.closest(".reply-write-disabled")) {
          window.toast.error("답글 작성은 로그인 후 이용 가능합니다.");
        }
      });
    }

    // 답글 토글 처리
    async function handleReplyToggle(commentEl, target) {
      const replyContainer = commentEl.querySelector(".reply-container");
      const repliesList = commentEl.querySelector(".replies-list");
      const figureId = target.dataset.figureId;
      const commentId = target.dataset.commentId;

      // 이미 표시 중이면 숨김
      if (!replyContainer.classList.contains("hidden")) {
        utils.fadeOut(replyContainer);
        setTimeout(() => {
          replyContainer.classList.add("hidden");
          repliesList.innerHTML = "";
        }, CONFIG.ANIMATION.DURATION);
        return;
      }

      try {
        // 로딩 표시
        const loadingMessage = document.createElement('div');
        loadingMessage.className = 'text-center py-2 text-gray-500';
        loadingMessage.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i> 답글을 불러오는 중...';
        repliesList.appendChild(loadingMessage);
        replyContainer.classList.remove("hidden");
        utils.fadeIn(replyContainer);

        // 스크롤 이동
        utils.smoothScrollTo(replyContainer, -100);

        // 답글 가져오기
        const replies = await CommentAPI.fetchReplies(figureId, commentId);
        repliesList.innerHTML = "";

        // 답글이 없으면 메시지 표시
        if (replies.length === 0) {
          const emptyMessage = document.createElement('div');
          emptyMessage.className = 'text-center py-4 text-gray-500';
          emptyMessage.textContent = '아직 답글이 없습니다.';
          repliesList.appendChild(emptyMessage);
          utils.fadeIn(emptyMessage);
        } else {
          // 답글 목록 표시
          replies.forEach(reply => {
            const replyEl = document.createElement("div");
            replyEl.className = "comment reply text-sm text-gray-700 border-t border-gray-100 pt-2 mt-2";
            replyEl.innerHTML = `
  <div class="flex items-center gap-2">
    <img src="${reply.userProfileImage || "/img/placeholder.svg"}" class="w-6 h-6 rounded-full object-cover" data-handle-error="true" data-placeholder="/img/profile-placeholder.svg">
    <span class="font-medium">${utils.escapeHtml(
                reply.userNickname || "익명")}</span>
    <span class="text-gray-400 text-xs">${utils.formatTimeAgo(
                reply.createdAt)}</span>
  </div>
  <div class="ml-8 mt-1">${utils.escapeHtml(reply.content)}</div>
  <div class="ml-8 mt-1 flex gap-4 text-xs text-gray-500">
    <span class="like-btn cursor-pointer ${reply.liked
                ? 'clicked text-blue-600' : ''}" data-id="${reply.id}">
      <i class="fa-regular fa-thumbs-up"></i> <span class="count">${reply.likeCount
            || 0}</span>
    </span>
    <span class="dislike-btn cursor-pointer ${reply.disliked
                ? 'clicked text-red-500' : ''}" data-id="${reply.id}">
      <i class="fa-regular fa-thumbs-down"></i> <span class="count">${reply.dislikeCount
            || 0}</span>
    </span>
  </div>
`;
            repliesList.appendChild(replyEl);
            utils.fadeIn(replyEl);
          });
        }
      } catch (err) {
        console.error("답글 로딩 실패:", err);
        window.toast.error("답글을 불러오는 중 오류가 발생했습니다.");

        // 에러 메시지 표시
        repliesList.innerHTML = `
          <div class="text-center py-3 text-red-500">
            <i class="fas fa-exclamation-circle mr-2"></i>답글을 불러오지 못했습니다.
          </div>
        `;
      }
    }

    // 답글 제출 처리
    async function handleReplySubmit(commentEl, target, commentId) {
      // 로그인 상태 재확인
      if (!state.isLoggedIn) {
        window.toast.error("답글 작성은 로그인 후 이용 가능합니다.");
        return;
      }

      const replyForm = commentEl.querySelector(".reply-form");
      const textarea = replyForm.querySelector(".reply-textarea");
      const repliesList = commentEl.querySelector(".replies-list");
      const content = textarea.value.trim();

      if (!content) {
        window.toast.error("답글 내용을 입력해주세요.");
        return;
      }

      // 버튼 상태 변경
      target.disabled = true;
      const originalText = target.textContent;
      target.textContent = "작성 중...";

      try {
        // API 호출
        const newReply = await CommentAPI.createReply(
            state.figureId,
            commentId,
            content
        );

        textarea.value = "";
        replyForm.classList.add("hidden");

        // 빈 답글 메시지 제거
        const emptyMessage = repliesList.querySelector('div.text-center.py-4');
        if (emptyMessage) {
          repliesList.removeChild(emptyMessage);
        }

        // 새 답글 요소 추가
        const replyEl = document.createElement("div");
        replyEl.className = "comment reply text-sm text-gray-700 border-t border-gray-100 pt-2 mt-2";
        replyEl.innerHTML = `
  <div class="ml-8 mt-1 flex gap-4 text-xs text-gray-500">
    <span class="like-btn cursor-pointer ${reply.liked
            ? 'clicked text-blue-600' : ''}" data-id="${reply.id}">
      <i class="fa-regular fa-thumbs-up"></i> <span class="count">${reply.likeCount
        || 0}</span>
    </span>
    <span class="dislike-btn cursor-pointer ${reply.disliked
            ? 'clicked text-red-500' : ''}" data-id="${reply.id}">
      <i class="fa-regular fa-thumbs-down"></i> <span class="count">${reply.dislikeCount
        || 0}</span>
    </span>
  </div>
`;
        repliesList.appendChild(replyEl);
        utils.fadeIn(replyEl);

        // 자동 스크롤
        utils.smoothScrollTo(replyEl, -100);

        // 답글 수 업데이트
        updateReplyCount(commentEl, commentId);

        window.toast.success("답글이 등록되었습니다.");
      } catch (err) {
        console.error("답글 작성 실패:", err);
        window.toast.error("답글 등록 중 문제가 발생했습니다.");
      } finally {
        // 버튼 상태 복구
        target.disabled = false;
        target.textContent = originalText;
      }
    }

    // 답글 수 업데이트
    function updateReplyCount(commentEl, commentId) {
      const replyToggle = commentEl.querySelector(".reply-toggle");
      if (replyToggle) {
        const matches = replyToggle.textContent.match(/(\d+)/);
        if (matches) {
          const newCount = parseInt(matches[1], 10) + 1;
          replyToggle.innerHTML = `<i class="fa-regular fa-comment-dots"></i> 답글 ${newCount}개`;
        } else {
          // 처음 답글이 달린 경우
          const newReplyToggle = document.createElement('span');
          newReplyToggle.className = 'reply-toggle cursor-pointer font-medium text-blue-600 hover:underline';
          newReplyToggle.dataset.commentId = commentId;
          newReplyToggle.dataset.figureId = state.figureId;
          newReplyToggle.innerHTML = `<i class="fa-regular fa-comment-dots"></i> 답글 1개`;
          commentEl.querySelector('.comment-footer').appendChild(
              newReplyToggle);
        }
      }
    }

    // 댓글 폼 이벤트 설정
    function setupCommentForm() {
      const commentForm = document.querySelector(CONFIG.SELECTORS.COMMENT_FORM);
      if (!commentForm) {
        return;
      }

      commentForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        // 로그인 상태 재확인
        if (!state.isLoggedIn) {
          window.toast.error("댓글 작성은 로그인 후 이용 가능합니다.");
          return;
        }

        const textarea = commentForm.querySelector("textarea[name='content']");
        const content = textarea.value.trim();

        if (!content) {
          window.toast.error("댓글 내용을 입력해주세요.");
          return;
        }

        // 버튼 비활성화 및 로딩 상태 표시
        const submitButton = commentForm.querySelector("button[type='submit']");
        const originalText = submitButton.innerHTML;
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fas fa-spinner fa-spin mr-1"></i> 게시 중...';

        try {
          // API 호출
          await CommentAPI.createComment(commentForm.action, content);

          textarea.value = "";
          window.toast.success("댓글이 등록되었습니다.");

          // 댓글 목록 새로고침
          const commentContainer = document.querySelector(
              CONFIG.SELECTORS.COMMENT_CONTAINER);
          state.page = 0;
          commentContainer.innerHTML = '';
          state.lastPage = false;
          fetchComments(commentContainer);
        } catch (err) {
          console.error("댓글 등록 중 오류:", err);
          window.toast.error(err.message || "댓글 등록 중 문제가 발생했습니다.");
        } finally {
          // 버튼 상태 복구
          submitButton.disabled = false;
          submitButton.innerHTML = originalText;
        }
      });
    }

    // 이미지 에러 처리
    function setupImageErrorHandling() {
      document.addEventListener('error', function (e) {
        const target = e.target;
        if (target.tagName === 'IMG' && target.hasAttribute(
            'data-handle-error')) {
          const placeholder = target.getAttribute('data-placeholder')
              || '/img/profile-placeholder.svg';
          target.src = placeholder;
        }
      }, true);
    }

    // 초기화 함수
    function init() {
      // Toast 매니저 초기화
      initializeToastManager();

      // DOM 요소 초기화
      const commentContainer = initializeDOM();
      if (!commentContainer) {
        return;
      }

      // 무한 스크롤 설정
      setupInfiniteScroll(commentContainer);

      // 댓글 이벤트 설정
      setupCommentEvents(commentContainer);

      // 댓글 폼 이벤트 설정
      setupCommentForm();

      // 이미지 에러 처리 설정
      setupImageErrorHandling();

      // 첫 댓글 목록 로드
      fetchComments(commentContainer);
    }

    // 공개 API
    return {
      init
    };
  })();

  // 댓글 모듈 초기화
  CommentModule.init();
});

/**
 * Toast 관리자 초기화 함수
 * 기존 함수를 수정하여 전역에서 접근 가능하게 함
 */
function initializeToastManager() {
  if (typeof window.toast === 'undefined') {
    window.toast = {
      success: (msg) => alert('성공: ' + msg),
      error: (msg) => alert('오류: ' + msg),
      warning: (msg) => alert('경고: ' + msg),
      info: (msg) => alert('알림: ' + msg)
    };
    console.warn('Toast 매니저가 로드되지 않았습니다. alert으로 대체합니다.');
  }
}