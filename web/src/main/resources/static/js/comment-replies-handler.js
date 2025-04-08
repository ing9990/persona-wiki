/**
 * 댓글 UI 핸들러
 * 댓글 관련 UI 이벤트를 처리합니다.
 */
document.addEventListener('DOMContentLoaded', function () {
  // 답글 토글 버튼 클릭 이벤트 처리
  document.addEventListener('click', async function (e) {
    // 답글 N개 버튼 클릭 처리
    if (e.target.closest('.reply-toggle')) {
      const replyToggleBtn = e.target.closest('.reply-toggle');
      const commentId = replyToggleBtn.dataset.commentId;
      const figureId = replyToggleBtn.dataset.figureId;
      const commentElement = replyToggleBtn.closest('.comment');
      const replyContainer = commentElement.querySelector('.reply-container');
      const repliesList = replyContainer.querySelector('.replies-list');

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

        // 좋아요/싫어요 버튼 이벤트 재등록
        initializeLikeDislikeButtons(repliesList);
      } catch (error) {
        console.error('답글 로딩 실패:', error);
        repliesList.innerHTML = '<div class="text-center py-2 text-red-500">답글을 불러오지 못했습니다. 다시 시도해주세요.</div>';
      }
    }

    // 답글 쓰기 버튼 클릭 처리
    if (e.target.closest('.reply-write-btn')) {
      const replyWriteBtn = e.target.closest('.reply-write-btn');
      const commentId = replyWriteBtn.dataset.id;
      const commentElement = replyWriteBtn.closest('.comment');
      const replyContainer = commentElement.querySelector('.reply-container');

      // 컨테이너가 숨겨져 있다면 보이게 함
      if (replyContainer.classList.contains('hidden')) {
        replyContainer.classList.remove('hidden');
      }

      // 이미 답글 폼이 있는지 확인
      let replyForm = replyContainer.querySelector('.reply-form');
      if (!replyForm) {
        // 답글 폼 생성
        const figureId = document.querySelector(
            'meta[name="figure-id"]').content;
        replyForm = createReplyForm(figureId, commentId);
        replyContainer.insertAdjacentHTML('afterbegin', replyForm);

        // 폼 이벤트 바인딩
        bindReplyFormEvents(replyContainer.querySelector('.reply-form'));
      } else {
        // 폼이 이미 있다면 포커스
        replyForm.querySelector('textarea').focus();
      }
    }
  });

  // 초기 좋아요/싫어요 버튼 이벤트 등록
  initializeLikeDislikeButtons(document);
});

/**
 * 답글 컨테이너 토글
 * @param {HTMLElement} container - 답글 컨테이너 요소
 */
function toggleReplyContainer(container) {
  if (container.classList.contains('hidden')) {
    container.classList.remove('hidden');
  } else {
    container.classList.add('hidden');
  }
}

/**
 * 답글 작성 폼 생성
 * @param {string} figureId - 피규어 ID
 * @param {string} commentId - 댓글 ID
 * @returns {string} - 폼 HTML
 */
function createReplyForm(figureId, commentId) {
  return `
    <div class="reply-form mb-4">
      <form data-figure-id="${figureId}" data-comment-id="${commentId}">
        <textarea
          class="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
          placeholder="답글을 입력하세요..."
          rows="2"
          required
        ></textarea>
        <div class="flex justify-end mt-2 gap-2">
          <button type="button" class="cancel-btn px-3 py-1 text-sm text-gray-600 hover:text-gray-800">
            취소
          </button>
          <button type="submit" class="submit-btn px-3 py-1 bg-blue-500 text-white rounded-md text-sm hover:bg-blue-600">
            등록
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
          `.comment [data-id="${commentId}"]`).closest('.comment');
      const repliesList = commentElement.querySelector(
          '.reply-container .replies-list');
      repliesList.innerHTML = repliesHtml;

      // 좋아요/싫어요 버튼 이벤트 재등록
      initializeLikeDislikeButtons(repliesList);

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
 * 좋아요/싫어요 버튼 초기화
 * @param {HTMLElement} container - 버튼을 포함하는 컨테이너
 */
function initializeLikeDislikeButtons(container) {
  // 좋아요 버튼 이벤트
  container.querySelectorAll('.like-btn').forEach(btn => {
    if (!btn.hasAttribute('data-initialized')) {
      btn.setAttribute('data-initialized', 'true');
      btn.addEventListener('click', handleLikeDislikeClick);
    }
  });

  // 싫어요 버튼 이벤트
  container.querySelectorAll('.dislike-btn').forEach(btn => {
    if (!btn.hasAttribute('data-initialized')) {
      btn.setAttribute('data-initialized', 'true');
      btn.addEventListener('click', handleLikeDislikeClick);
    }
  });
}

/**
 * 좋아요/싫어요 버튼 클릭 핸들러
 * @param {Event} e - 클릭 이벤트
 */
async function handleLikeDislikeClick(e) {
  const btn = e.currentTarget;
  const commentId = btn.dataset.id;
  const figureId = btn.dataset.figureId;

  try {
    await CommentAPI.toggleLikeDislike(figureId, commentId);

    // 클릭 상태 토글
    btn.classList.toggle('clicked');

    // 좋아요일 경우 색상 변경
    if (btn.classList.contains('like-btn')) {
      if (btn.classList.contains('clicked')) {
        btn.classList.add('text-blue-600');
      } else {
        btn.classList.remove('text-blue-600');
      }
    }

    // 싫어요일 경우 색상 변경
    if (btn.classList.contains('dislike-btn')) {
      if (btn.classList.contains('clicked')) {
        btn.classList.add('text-red-500');
      } else {
        btn.classList.remove('text-red-500');
      }
    }

    // 카운트 업데이트 로직 필요 (서버에서 실시간으로 받아와야 정확함)
  } catch (error) {
    console.error('좋아요/싫어요 처리 실패:', error);
  }
}

/**
 * 답글 카운트 업데이트
 * @param {HTMLElement} commentElement - 댓글 요소
 */
function updateReplyCount(commentElement) {
  const replyToggle = commentElement.querySelector('.reply-toggle');
  if (!replyToggle) {
    // 답글이 없었던 경우 새로운 토글 버튼 추가
    const commentFooter = commentElement.querySelector('.comment-footer');
    const figureId = document.querySelector('meta[name="figure-id"]').content;
    const commentId = commentElement.querySelector('[data-id]').dataset.id;

    const newToggleBtn = document.createElement('span');
    newToggleBtn.className = 'reply-toggle cursor-pointer font-medium text-blue-600 hover:underline';
    newToggleBtn.dataset.commentId = commentId;
    newToggleBtn.dataset.figureId = figureId;
    newToggleBtn.innerHTML = '<i class="fa-regular fa-comment-dots"></i> 답글 <span>1</span>개';

    commentFooter.appendChild(newToggleBtn);
  } else {
    // 기존 토글 버튼 업데이트
    const countSpan = replyToggle.querySelector('span');
    const currentCount = parseInt(countSpan.textContent);
    countSpan.textContent = currentCount + 1;
  }
}