/**
 * 댓글 답글 UI 핸들러
 * 답글 보기/숨기기 및 답글 작성 UI 이벤트를 처리합니다.
 */

// 상수로 선택자 정의
const REPLY_UI_SELECTORS = {
  COMMENT_ITEM: '.comment-item',
  REPLY_BUTTON: '.reply-button',     // 답글 작성 버튼
  VIEW_REPLIES_BTN: '.view-replies-btn', // 답글 보기/숨기기 버튼
  REPLY_CONTAINER: '.reply-container',
  REPLIES_LIST: '.replies-list',
  REPLY_COUNT: '.reply-count',
  REPLY_FORM: '.reply-form',
};

document.addEventListener('DOMContentLoaded', function () {
  // 답글 관련 UI 이벤트 처리
  document.addEventListener('click', async function (e) {
    // 댓글의 답글 버튼 클릭 처리 (답글 작성 폼 표시)
    if (e.target.closest(REPLY_UI_SELECTORS.REPLY_BUTTON)) {
      handleReplyButtonClick(e);
    }

    // 답글 보기 버튼 클릭 처리 (답글 목록 보기)
    if (e.target.closest(REPLY_UI_SELECTORS.VIEW_REPLIES_BTN)) {
      handleViewRepliesButtonClick(e);
    }

    // 답글 쓰기 버튼 클릭 처리
    if (e.target.closest('.reply-write-btn')) {
      handleReplyWriteButtonClick(e);
    }
  });
});

/**
 * 답글 버튼 클릭 처리 (답글 작성)
 * @param {Event} e - 클릭 이벤트
 */
function handleReplyButtonClick(e) {
  const replyButton = e.target.closest(REPLY_UI_SELECTORS.REPLY_BUTTON);
  const commentId = replyButton.dataset.id;
  const commentElement = replyButton.closest(REPLY_UI_SELECTORS.COMMENT_ITEM);
  const replyContainer = commentElement.querySelector(
      REPLY_UI_SELECTORS.REPLY_CONTAINER);

  // 컨테이너가 숨겨져 있다면 보이게 함
  if (replyContainer.classList.contains('hidden')) {
    replyContainer.classList.remove('hidden');
  }

  // 이미 답글 폼이 있는지 확인
  let replyForm = replyContainer.querySelector(REPLY_UI_SELECTORS.REPLY_FORM);
  if (!replyForm) {
    // 답글 폼 생성
    const figureId = document.querySelector('#comment-list').dataset.figureId;
    const formHtml = createReplyForm(figureId, commentId);
    replyContainer.insertAdjacentHTML('afterbegin', formHtml);

    // 폼 이벤트 바인딩
    bindReplyFormEvents(
        replyContainer.querySelector(REPLY_UI_SELECTORS.REPLY_FORM));
  } else {
    // 폼이 이미 있다면 포커스
    replyForm.querySelector('textarea').focus();
  }
}

/**
 * 답글 보기 버튼 클릭 처리 (답글 목록 보기)
 * @param {Event} e - 클릭 이벤트
 */
async function handleViewRepliesButtonClick(e) {
  const viewRepliesBtn = e.target.closest(REPLY_UI_SELECTORS.VIEW_REPLIES_BTN);
  const commentId = viewRepliesBtn.dataset.id;
  const figureId = document.querySelector('#comment-list').dataset.figureId;
  const commentElement = viewRepliesBtn.closest(
      REPLY_UI_SELECTORS.COMMENT_ITEM);
  const replyContainer = commentElement.querySelector(
      REPLY_UI_SELECTORS.REPLY_CONTAINER);
  const repliesList = replyContainer.querySelector(
      REPLY_UI_SELECTORS.REPLIES_LIST);

  // 이미 로드된 경우 토글만 수행
  if (repliesList.innerHTML.trim() !== '') {
    toggleReplyContainer(replyContainer);
    return;
  }

  try {
    // 로딩 표시
    repliesList.innerHTML = '<div class="text-center py-2"><i class="fas fa-spinner fa-spin"></i> 답글을 불러오는 중...</div>';
    replyContainer.classList.remove('hidden');

    // 답글 Fragment 로드
    const repliesHtml = await CommentAPI.fetchRepliesFragment(figureId,
        commentId);

    // 응답 HTML을 삽입
    repliesList.innerHTML = repliesHtml;

    // 좋아요/싫어요 버튼 이벤트 재등록 - CommentInteractions 모듈 사용
    if (window.CommentInteractions) {
      window.CommentInteractions.initializeAllLikeDislikeButtons(repliesList);
    }
  } catch (error) {
    console.error('답글 로딩 실패:', error);
    repliesList.innerHTML = '<div class="text-center py-2 text-red-500">답글을 불러오지 못했습니다. 다시 시도해주세요.</div>';
  }
}

/**
 * 답글 컨테이너 토글
 * @param {HTMLElement} container - 답글 컨테이너 요소
 */
function toggleReplyContainer(container) {
  container.classList.toggle('hidden');
}

/**
 * 답글 쓰기 버튼 클릭 처리
 * @param {Event} e - 클릭 이벤트
 */
function handleReplyWriteButtonClick(e) {
  const replyWriteBtn = e.target.closest('.reply-write-btn');
  const commentId = replyWriteBtn.dataset.id;
  const commentElement = replyWriteBtn.closest(REPLY_UI_SELECTORS.COMMENT_ITEM);
  const replyContainer = commentElement.querySelector(
      REPLY_UI_SELECTORS.REPLY_CONTAINER);

  // 컨테이너가 숨겨져 있다면 보이게 함
  if (replyContainer.classList.contains('hidden')) {
    replyContainer.classList.remove('hidden');
  }

  // 이미 답글 폼이 있는지 확인
  let replyForm = replyContainer.querySelector(REPLY_UI_SELECTORS.REPLY_FORM);
  if (!replyForm) {
    // 답글 폼 생성
    const figureId = document.querySelector('#comment-list').dataset.figureId;
    replyForm = createReplyForm(figureId, commentId);
    replyContainer.insertAdjacentHTML('afterbegin', replyForm);

    // 폼 이벤트 바인딩
    bindReplyFormEvents(
        replyContainer.querySelector(REPLY_UI_SELECTORS.REPLY_FORM));
  } else {
    // 폼이 이미 있다면 포커스
    replyForm.querySelector('textarea').focus();
  }
}

/**
 * 답글 폼 생성
 * @param {string} figureId - 피규어 ID
 * @param {string} commentId - 댓글 ID
 * @returns {string} - 폼 HTML
 */
function createReplyForm(figureId, commentId) {
  return `
    <div class="add-reply-form mb-4">
      <form data-figure-id="${figureId}" data-comment-id="${commentId}">
        <div class="input-wrapper">
          <input
            class="requires-login w-full py-1 bg-transparent border-0 focus:outline-none focus:ring-0 placeholder-gray-500"
            placeholder="답글을 입력하세요..."
            rows="2"
            required
          ></input>
        </div>
        <div class="button-group flex justify-end mt-2 gap-2">
          <button type="button" class="cancel-btn button-custom">
            취소
          </button>
          <button type="submit" class="submit-btn button-custom requires-login">
            <i class="fas fa-paper-plane mr-1"></i> 등록
          </button>
        </div>
      </form>
    </div>
  `;
}

/**
 * 답글 폼 이벤트 바인딩
 * @param {HTMLElement} formElement - 폼 요소
 */
function bindReplyFormEvents(formElement) {
  const form = formElement.querySelector('form');
  const textarea = form.querySelector('input');
  const cancelBtn = form.querySelector('.cancel-btn');
  const submitBtn = form.querySelector('.submit-btn');

  // 로그인 체크 재적용
  reapplyLoginCheck();

  // 취소 버튼 이벤트
  if (cancelBtn) {
    cancelBtn.addEventListener('click', function () {
      formElement.remove();
    });
  }

  // 폼 제출 이벤트
  if (form) {
    form.addEventListener('submit', async function (e) {
      e.preventDefault();

      // 로그인 상태 확인
      const isLoggedIn = document.getElementById('userLoginStatus')?.value
          === 'true';
      if (!isLoggedIn) {
        // 로그인 모달 표시
        if (typeof window.openLoginModal === 'function') {
          window.openLoginModal();
        }
        return;
      }

      const figureId = form.dataset.figureId;
      const commentId = form.dataset.commentId;
      const content = textarea.value.trim();

      if (!content) {
        return;
      }

      try {
        // 로딩 상태 표시
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-1"></i> 등록 중...';
        submitBtn.classList.add('opacity-75');

        // 답글 등록 요청 (Fragment 방식)
        const repliesHtml = await CommentAPI.createReplyFragment(figureId,
            commentId, content);

        // 폼 제거 및 답글 목록 갱신
        formElement.remove();

        // 댓글 아래 replies-list 요소를 찾아서 업데이트
        const commentElement = document.querySelector(
            `${SELECTORS.COMMENT_ITEM} [data-id="${commentId}"]`).closest(
            SELECTORS.COMMENT_ITEM);
        const repliesList = commentElement.querySelector(
            `${SELECTORS.REPLY_CONTAINER} ${SELECTORS.REPLIES_LIST}`);

        if (repliesList) {
          repliesList.innerHTML = repliesHtml;

          // 좋아요/싫어요 버튼 이벤트 재등록 - CommentInteractions 모듈 사용
          if (window.CommentInteractions) {
            window.CommentInteractions.initializeAllLikeDislikeButtons(
                repliesList);
          }

          // 답글 컨테이너를 보이게 설정
          const replyContainer = commentElement.querySelector(
              SELECTORS.REPLY_CONTAINER);
          if (replyContainer && replyContainer.classList.contains('hidden')) {
            replyContainer.classList.remove('hidden');
          }

          // 답글 카운트 업데이트
          updateReplyCount(commentElement);
        } else {
          console.error('답글 목록 요소를 찾을 수 없습니다.');
        }
      } catch (error) {
        console.error('답글 등록 실패:', error);
        alert('답글 등록에 실패했습니다. 다시 시도해주세요.');

        // 버튼 상태 복구
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-paper-plane mr-1"></i> 등록';
        submitBtn.classList.remove('opacity-75');
      }
    });
  }

  // 포커스
  if (textarea) {
    textarea.focus();
  }
}

/**
 * 동적으로 추가된 요소에 로그인 체크 적용
 */
function reapplyLoginCheck() {
  const requiresLoginElements = document.querySelectorAll('.requires-login');
  const isLoggedIn = document.getElementById('userLoginStatus')?.value
      === 'true';

  // 이미 로그인된 상태면 아무 처리 안함
  if (isLoggedIn) {
    return;
  }

  // 로그인이 필요한 요소들에 이벤트 리스너 추가
  requiresLoginElements.forEach(element => {
    // 이미 이벤트가 등록되어 있으면 건너뜀 (중복 등록 방지)
    if (element.hasAttribute('data-login-check-applied')) {
      return;
    }

    element.setAttribute('data-login-check-applied', 'true');

    element.addEventListener('click', function (e) {
      // submit 버튼인 경우 폼 제출 방지
      if (element.type === 'submit') {
        e.preventDefault();
      }

      // 로그인 모달 표시
      if (typeof window.openLoginModal === 'function') {
        window.openLoginModal();
      }
    });

    element.addEventListener('focus', function (e) {
      // 로그인 모달 표시
      if (typeof window.openLoginModal === 'function') {
        window.openLoginModal();
      }
      // 포커스 해제
      element.blur();
    });
  });
}

/**
 * 답글 폼 이벤트 바인딩
 * @param {HTMLElement} formElement - 폼 요소
 */
function bindReplyFormEvents(formElement) {
  const form = formElement.querySelector('form');
  const textarea = form.querySelector('textarea');
  const cancelBtn = form.querySelector('.cancel-btn');

  // 취소 버튼 이벤트
  cancelBtn.addEventListener('click', function () {
    formElement.remove();
  });

  // 폼 제출 이벤트
  form.addEventListener('submit', async function (e) {
    e.preventDefault();

    const figureId = form.dataset.figureId;
    const commentId = form.dataset.commentId;
    const content = textarea.value.trim();

    if (!content) {
      return;
    }

    try {
      // 로딩 상태 표시
      const submitBtn = form.querySelector('.submit-btn');
      const originalText = submitBtn.textContent;
      submitBtn.disabled = true;
      submitBtn.textContent = '등록 중...';

      // 답글 등록 요청 (Fragment 방식)
      const repliesHtml = await CommentAPI.createReplyFragment(figureId,
          commentId, content);

      // 폼 제거 및 답글 목록 갱신
      formElement.remove();

      // 댓글 아래 replies-list 요소를 찾아서 업데이트
      const commentElement = document.querySelector(
          `${REPLY_UI_SELECTORS.COMMENT_ITEM} [data-id="${commentId}"]`).closest(
          REPLY_UI_SELECTORS.COMMENT_ITEM);
      const repliesList = commentElement.querySelector(
          `${REPLY_UI_SELECTORS.REPLY_CONTAINER} ${REPLY_UI_SELECTORS.REPLIES_LIST}`);
      repliesList.innerHTML = repliesHtml;

      // 좋아요/싫어요 버튼 이벤트 재등록 - CommentInteractions 모듈 사용
      if (window.CommentInteractions) {
        window.CommentInteractions.initializeAllLikeDislikeButtons(repliesList);
      }

      // 답글 카운트 업데이트
      updateReplyCount(commentElement);
    } catch (error) {
      console.error('답글 등록 실패:', error);
      alert('답글 등록에 실패했습니다. 다시 시도해주세요.');

      // 버튼 상태 복구
      submitBtn.disabled = false;
      submitBtn.textContent = originalText;
    }
  });

  // 포커스
  textarea.focus();
}

/**
 * 답글 카운트 업데이트
 * @param {HTMLElement} commentElement - 댓글 요소
 */
function updateReplyCount(commentElement) {
  // '답글 보기' 버튼 찾기
  const viewRepliesBtn = commentElement.querySelector(
      REPLY_UI_SELECTORS.VIEW_REPLIES_BTN);
  const replyCountElement = viewRepliesBtn ? viewRepliesBtn.querySelector(
      REPLY_UI_SELECTORS.REPLY_COUNT) : null;

  // 현재 답글 카운트 가져오기
  let currentCount = 0;
  if (replyCountElement) {
    const countText = replyCountElement.textContent;
    const match = countText.match(/답글 (\d+)개/);
    currentCount = match ? parseInt(match[1]) : 0;
  }

  // 응답 카운트 증가
  const newCount = currentCount + 1;

  if (viewRepliesBtn) {
    // 이미 버튼이 있으면 카운트만 업데이트
    if (replyCountElement) {
      replyCountElement.textContent = `답글 ${newCount}개`;
    }
  } else {
    // 버튼이 없으면 새로 생성
    const replyButton = commentElement.querySelector(
        REPLY_UI_SELECTORS.REPLY_BUTTON);
    if (replyButton) {
      const newViewRepliesBtn = document.createElement('button');
      newViewRepliesBtn.className = 'view-replies-btn flex items-center ml-2 text-blue-500 hover:text-blue-700';
      newViewRepliesBtn.dataset.id = replyButton.dataset.id;

      const countSpan = document.createElement('span');
      countSpan.className = 'reply-count';
      countSpan.textContent = `답글 ${newCount}개`;

      newViewRepliesBtn.appendChild(countSpan);

      // 답글 버튼 다음에 삽입
      replyButton.parentNode.insertBefore(newViewRepliesBtn,
          replyButton.nextSibling);
    }
  }
}