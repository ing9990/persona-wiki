/**
 * 답글 보기 핸들러
 * 답글 목록 보기/숨기기 기능을 처리합니다.
 */

// 모듈 패턴으로 구현
const ViewRepliesHandler = (function () {
  // 선택자 정의
  const SELECTORS = {
    COMMENT_ITEM: '.comment-item',
    VIEW_REPLIES_BTN: '.view-replies-btn',
    REPLY_CONTAINER: '.reply-container',
    REPLIES_LIST: '.replies-list'
  };

  /**
   * 초기화 함수
   */
  function init() {
    // 이벤트 바인딩
    document.addEventListener('click', function (e) {
      // 답글 보기 버튼 클릭 처리
      if (e.target.closest(SELECTORS.VIEW_REPLIES_BTN)) {
        handleViewRepliesButtonClick(e);
      }
    });

    console.log('[ViewRepliesHandler] 초기화 완료');
  }

  /**
   * 답글 보기 버튼 클릭 처리
   * @param {Event} e - 클릭 이벤트
   */
  async function handleViewRepliesButtonClick(e) {
    const viewRepliesBtn = e.target.closest(SELECTORS.VIEW_REPLIES_BTN);
    const commentId = viewRepliesBtn.dataset.id;
    const figureId = document.querySelector('#comment-list').dataset.figureId;
    const commentElement = viewRepliesBtn.closest(SELECTORS.COMMENT_ITEM);
    const replyContainer = commentElement.querySelector(
        SELECTORS.REPLY_CONTAINER);
    const repliesList = replyContainer.querySelector(SELECTORS.REPLIES_LIST);

    // 이미 로드된 경우 토글만 수행
    if (repliesList.innerHTML.trim() !== '' &&
        !repliesList.querySelector('.loading-indicator') &&
        !repliesList.querySelector('.error-message')) {
      toggleReplyContainer(replyContainer);
      return;
    }

    try {
      // 로딩 표시
      repliesList.innerHTML = '<div class="loading-indicator text-center py-2"><i class="fas fa-spinner fa-spin"></i> 답글을 불러오는 중...</div>';
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

      RelativeTimeUtils.updateAllRelativeTimes(".comment-date-relative")
    } catch (error) {
      console.error('답글 로딩 실패:', error);
      repliesList.innerHTML = '<div class="error-message text-center py-2 text-red-500">답글을 불러오지 못했습니다. 다시 시도해주세요.</div>';
    }
  }

  /**
   * 답글 컨테이너 토글
   * @param {HTMLElement} container - 답글 컨테이너 요소
   */
  function toggleReplyContainer(container) {
    container.classList.toggle('hidden');
  }

  // 공개 API
  return {
    init: init
  };
})();

// DOM 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', function () {
  ViewRepliesHandler.init();
});