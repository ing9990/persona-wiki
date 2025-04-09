/**
 * 답글 폼 관리 핸들러
 * 답글 작성 폼 표시 및 제출을 처리합니다.
 */

// 모듈 패턴으로 구현
const AddReplyFormHandler = (function () {
  // 선택자 정의
  const SELECTORS = {
    COMMENT_ITEM: '.comment-item',
    ADD_REPLY_BUTTON: '.add-reply-button',
    REPLY_CONTAINER: '.reply-container',
    REPLY_FORM: '.add-reply-form',
    REPLIES_LIST: '.replies-list'
  };

  /**
   * 초기화 함수
   */
  function init() {
    // 이벤트 바인딩
    document.addEventListener('click', function (e) {
      // 답글 작성 버튼 클릭 처리
      if (e.target.closest(SELECTORS.ADD_REPLY_BUTTON)) {
        handleButtonClick(e);
      }
    });

    console.log('[AddReplyFormHandler] 초기화 완료');
  }

  /**
   * 답글 작성 버튼 클릭 이벤트 처리
   * @param {Event} e - 클릭 이벤트
   */
  function handleButtonClick(e) {
    console.log('답글 작성 버튼 클릭 감지');
    const addReplyBtn = e.target.closest(SELECTORS.ADD_REPLY_BUTTON);
    const commentId = addReplyBtn.dataset.id;
    const commentElement = addReplyBtn.closest(SELECTORS.COMMENT_ITEM);
    const replyContainer = commentElement.querySelector(
        SELECTORS.REPLY_CONTAINER);

    // 컨테이너가 숨겨져 있다면 보이게 함
    if (replyContainer.classList.contains('hidden')) {
      replyContainer.classList.remove('hidden');
    }

    // 이미 답글 폼이 있는지 확인
    let replyForm = replyContainer.querySelector(SELECTORS.REPLY_FORM);

    console.log('답글 폼 존재 여부:', !!replyForm);

    if (!replyForm) {
      // 답글 폼 생성
      const figureId = document.querySelector('#comment-list').dataset.figureId;
      const formHtml = createReplyForm(figureId, commentId);

      // 폼을 컨테이너 제일 위에 삽입
      replyContainer.insertAdjacentHTML('afterbegin', formHtml);

      // 폼 요소 찾기
      replyForm = replyContainer.querySelector(SELECTORS.REPLY_FORM);

      // 폼 이벤트 바인딩
      if (replyForm) {
        bindReplyFormEvents(replyForm);
        console.log('답글 폼 생성 및 이벤트 바인딩 완료');
      } else {
        console.error('답글 폼을 생성했으나 DOM에서 찾을 수 없습니다.');
      }
    } else {
      // 폼이 이미 있다면 포커스
      const textarea = replyForm.querySelector('input');
      if (textarea) {
        textarea.focus();
      }
    }
  }

  /**
   * 답글 작성 폼 생성 - 댓글 작성 폼과 동일한 스타일로 변경
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
              class="w-full py-1 bg-transparent border-0 focus:outline-none focus:ring-0 placeholder-gray-500"
              placeholder="답글을 입력하세요..."
              rows="2"
              required
            ></input>
          </div>
          <div class="button-group flex justify-end mt-2 gap-2">
            <button type="button" class="cancel-btn button-custom">
              취소
            </button>
            <button type="submit" class="submit-btn button-custom">
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

        const figureId = form.dataset.figureId;
        const commentId = form.dataset.commentId;
        const content = textarea.value.trim();

        if (!content) {
          return;
        }

        try {
          // 로딩 상태 표시
          const submitBtn = form.querySelector('.submit-btn');
          const originalText = submitBtn.innerHTML;
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
          submitBtn.innerHTML = originalText;
        }
      });
    }

    // 포커스
    if (textarea) {
      textarea.focus();
    }
  }

  /**
   * 답글 카운트 업데이트
   * @param {HTMLElement} commentElement - 댓글 요소
   */
  function updateReplyCount(commentElement) {
    // '답글 N개' 버튼 찾기
    const viewRepliesBtn = commentElement.querySelector('.view-replies-btn');
    const replyCountElement = viewRepliesBtn ? viewRepliesBtn.querySelector(
        '.reply-count') : null;

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
      const addReplyButton = commentElement.querySelector(
          SELECTORS.ADD_REPLY_BUTTON);
      if (addReplyButton) {
        const newViewRepliesBtn = document.createElement('button');
        newViewRepliesBtn.className = 'view-replies-btn flex items-center ml-4 text-blue-500 hover:text-blue-700 text-base';
        newViewRepliesBtn.dataset.id = addReplyButton.dataset.id;

        const countSpan = document.createElement('span');
        countSpan.className = 'reply-count';
        countSpan.textContent = `답글 ${newCount}개`;

        newViewRepliesBtn.appendChild(countSpan);

        // 답글 버튼 다음에 삽입
        addReplyButton.parentNode.insertBefore(newViewRepliesBtn,
            addReplyButton.nextSibling);
      }
    }
  }

  // 공개 API
  return {
    init: init,
    handleButtonClick: handleButtonClick  // 외부에서 직접 호출할 수 있도록 노출
  };
})();

// 전역으로 노출
window.AddReplyFormHandler = AddReplyFormHandler;

// DOM 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', function () {
  AddReplyFormHandler.init();
});