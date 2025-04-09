/**
 * 댓글 및 답글 상호작용 핸들러
 * 좋아요/싫어요 등의 상호작용 이벤트를 처리합니다.
 */

// 상수로 선택자 정의
const INTERACTION_SELECTORS = {
  // 댓글 관련 선택자
  COMMENT_ITEM: '.comment-item',
  LIKE_BUTTON: '.like-button',  // 댓글 좋아요 버튼
  DISLIKE_BUTTON: '.dislike-button',  // 댓글 싫어요 버튼

  // 답글 관련 선택자
  REPLY_LIKE_BTN: '.like-btn',  // 답글 좋아요 버튼
  REPLY_DISLIKE_BTN: '.dislike-btn',  // 답글 싫어요 버튼
};

document.addEventListener('DOMContentLoaded', function () {
  // 좋아요/싫어요 버튼 이벤트 처리
  document.addEventListener('click', async function (e) {
    // 댓글 좋아요 버튼 클릭 처리
    if (e.target.closest(INTERACTION_SELECTORS.LIKE_BUTTON)) {
      handleLikeDislikeClick(e, 'comment', 'LIKE');
    }

    // 댓글 싫어요 버튼 클릭 처리
    if (e.target.closest(INTERACTION_SELECTORS.DISLIKE_BUTTON)) {
      handleLikeDislikeClick(e, 'comment', 'DISLIKE');
    }

    // 답글 좋아요 버튼 클릭 처리
    if (e.target.closest(INTERACTION_SELECTORS.REPLY_LIKE_BTN)) {
      handleLikeDislikeClick(e, 'reply', 'LIKE');
    }

    // 답글 싫어요 버튼 클릭 처리
    if (e.target.closest(INTERACTION_SELECTORS.REPLY_DISLIKE_BTN)) {
      handleLikeDislikeClick(e, 'reply', 'DISLIKE');
    }
  });

  // 초기 좋아요/싫어요 버튼 이벤트 등록
  initializeAllLikeDislikeButtons();
});

/**
 * 모든 좋아요/싫어요 버튼 초기화
 * @param {HTMLElement} container - 버튼을 포함하는 컨테이너, 기본값은 document
 */
function initializeAllLikeDislikeButtons(container = document) {
  // 댓글 좋아요 버튼
  container.querySelectorAll(INTERACTION_SELECTORS.LIKE_BUTTON).forEach(btn => {
    if (!btn.hasAttribute('data-initialized')) {
      btn.setAttribute('data-initialized', 'true');
      // 여기서는 이벤트 위임 방식을 사용하므로 이벤트 리스너를 직접 추가할 필요 없음
    }
  });

  // 댓글 싫어요 버튼
  container.querySelectorAll(INTERACTION_SELECTORS.DISLIKE_BUTTON).forEach(btn => {
    if (!btn.hasAttribute('data-initialized')) {
      btn.setAttribute('data-initialized', 'true');
      // 여기서는 이벤트 위임 방식을 사용하므로 이벤트 리스너를 직접 추가할 필요 없음
    }
  });

  // 답글 좋아요 버튼
  container.querySelectorAll(INTERACTION_SELECTORS.REPLY_LIKE_BTN).forEach(btn => {
    if (!btn.hasAttribute('data-initialized')) {
      btn.setAttribute('data-initialized', 'true');
      // 여기서는 이벤트 위임 방식을 사용하므로 이벤트 리스너를 직접 추가할 필요 없음
    }
  });

  // 답글 싫어요 버튼
  container.querySelectorAll(INTERACTION_SELECTORS.REPLY_DISLIKE_BTN).forEach(btn => {
    if (!btn.hasAttribute('data-initialized')) {
      btn.setAttribute('data-initialized', 'true');
      // 여기서는 이벤트 위임 방식을 사용하므로 이벤트 리스너를 직접 추가할 필요 없음
    }
  });
}

/**
 * 좋아요/싫어요 버튼 클릭 핸들러
 * @param {Event} e - 클릭 이벤트
 * @param {string} type - 'comment' 또는 'reply'
 * @param {string} action - 'LIKE' 또는 'DISLIKE'
 */
async function handleLikeDislikeClick(e, type, action) {
  let clickedBtn, oppositeBtn, clickedCountEl, oppositeCountEl;

  if (type === 'comment') {
    // 댓글의 경우
    clickedBtn = e.target.closest(
        action === 'LIKE' ? INTERACTION_SELECTORS.LIKE_BUTTON : INTERACTION_SELECTORS.DISLIKE_BUTTON);
    const commentItem = clickedBtn.closest(INTERACTION_SELECTORS.COMMENT_ITEM);

    // 반대 버튼 찾기
    oppositeBtn = commentItem.querySelector(
        action === 'LIKE' ? INTERACTION_SELECTORS.DISLIKE_BUTTON : INTERACTION_SELECTORS.LIKE_BUTTON);

    // 카운트 요소 찾기
    clickedCountEl = clickedBtn.querySelector('.like-count, .dislike-count');
    oppositeCountEl = oppositeBtn.querySelector('.like-count, .dislike-count');
  } else {
    // 답글의 경우
    clickedBtn = e.target.closest(action === 'LIKE' ? INTERACTION_SELECTORS.REPLY_LIKE_BTN
        : INTERACTION_SELECTORS.REPLY_DISLIKE_BTN);
    const replyItem = clickedBtn.closest('.reply');

    // 반대 버튼 찾기
    oppositeBtn = replyItem.querySelector(
        action === 'LIKE' ? INTERACTION_SELECTORS.REPLY_DISLIKE_BTN
            : INTERACTION_SELECTORS.REPLY_LIKE_BTN);

    // 카운트 요소 찾기
    clickedCountEl = clickedBtn.querySelector('.count');
    oppositeCountEl = oppositeBtn.querySelector('.count');
  }

  // ID 정보 가져오기
  const commentId = clickedBtn.dataset.id;
  const figureId = clickedBtn.dataset.figureId || document.querySelector(
      '#comment-list').dataset.figureId;

  // 현재 카운트 값 가져오기
  const clickedCount = parseInt(clickedCountEl.textContent) || 0;
  const oppositeCount = parseInt(oppositeCountEl.textContent) || 0;

  try {
    // API 호출 (action 파라미터 추가)
    await CommentAPI.toggleLikeDislike(figureId, commentId, action);

    // 상태 업데이트 (현재 버튼)
    const wasClicked = clickedBtn.classList.contains('clicked');
    clickedBtn.classList.toggle('clicked');

    // 반대 버튼이 활성화되어 있으면 비활성화
    const wasOppositeClicked = oppositeBtn.classList.contains('clicked');
    if (wasOppositeClicked) {
      oppositeBtn.classList.remove('clicked');
    }

    // 카운트 업데이트 (현재 버튼)
    if (clickedBtn.classList.contains('clicked') && !wasClicked) {
      clickedCountEl.textContent = clickedCount + 1;
    } else if (!clickedBtn.classList.contains('clicked') && wasClicked) {
      clickedCountEl.textContent = Math.max(0, clickedCount - 1);
    }

    // 반대 버튼 카운트 업데이트 (반대 버튼이 활성화되어 있었다면)
    if (wasOppositeClicked) {
      oppositeCountEl.textContent = Math.max(0, oppositeCount - 1);
    }

    // 색상 변경 처리
    updateButtonStyles(clickedBtn, oppositeBtn, type, action);

  } catch (error) {
    console.error('좋아요/싫어요 처리 실패:', error);
  }
}

/**
 * 버튼 스타일 업데이트
 * @param {HTMLElement} clickedBtn - 클릭된 버튼
 * @param {HTMLElement} oppositeBtn - 반대 버튼
 * @param {string} type - 'comment' 또는 'reply'
 * @param {string} action - 'LIKE' 또는 'DISLIKE'
 */
function updateButtonStyles(clickedBtn, oppositeBtn, type, action) {
  if (type === 'comment') {
    // 댓글 버튼 스타일 업데이트
    if (action === 'LIKE') {
      // 좋아요 버튼 스타일
      clickedBtn.classList.toggle('text-indigo-600',
          clickedBtn.classList.contains('clicked'));
      // 싫어요 버튼 스타일 항상 제거
      oppositeBtn.classList.remove('text-red-600');
    } else {
      // 싫어요 버튼 스타일
      clickedBtn.classList.toggle('text-red-600',
          clickedBtn.classList.contains('clicked'));
      // 좋아요 버튼 스타일 항상 제거
      oppositeBtn.classList.remove('text-indigo-600');
    }
  } else {
    // 답글 버튼 스타일 업데이트
    if (action === 'LIKE') {
      // 좋아요 버튼 스타일
      clickedBtn.classList.toggle('text-blue-600',
          clickedBtn.classList.contains('clicked'));
      // 싫어요 버튼 스타일 항상 제거
      oppositeBtn.classList.remove('text-red-500');
    } else {
      // 싫어요 버튼 스타일
      clickedBtn.classList.toggle('text-red-500',
          clickedBtn.classList.contains('clicked'));
      // 좋아요 버튼 스타일 항상 제거
      oppositeBtn.classList.remove('text-blue-600');
    }
  }
}

// 모듈 노출
window.CommentInteractions = {
  initializeAllLikeDislikeButtons
};