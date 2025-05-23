/**
 * 댓글 생성 처리 모듈
 * 댓글 작성 기능을 관리합니다.
 */
var commentHandler = (function () {
  // 모듈 상태 변수
  var isInitialized = false;
  var isSubmitting = false;

  // DOM 요소 참조
  var elements = {
    form: null,
    textarea: null, // 실제로는 input 요소지만 호환성을 위해 이름 유지
    submitButton: null,
    cancelButton: null,
    commentList: null,
    template: null,
    noCommentsMessage: null,
    emptyCommentMessage: null
  };

  /**
   * 초기화 함수
   */
  function init() {
    // 중복 초기화 방지
    if (isInitialized) {
      return;
    }

    // DOM 요소 캐시
    elements.form = document.getElementById('comment-form');
    elements.textarea = document.getElementById('comment-textarea');
    elements.submitButton = document.getElementById('comment-submit-button');
    elements.cancelButton = document.getElementById('comment-cancel-button');
    elements.commentList = document.getElementById('comment-list');
    elements.template = document.getElementById('comment-template');
    elements.noCommentsMessage = document.querySelector('.js-no-comments');
    elements.emptyCommentMessage = document.querySelector(
        '.js-empty-comment-message');

    // DOM 요소 존재 확인
    if (!elements.form || !elements.textarea || !elements.submitButton
        || !elements.commentList || !elements.template) {
      console.error('댓글 기능에 필요한 DOM 요소를 찾을 수 없습니다.');
      return;
    }

    // 이벤트 리스너 제거 및 등록
    removeAllEventListeners();
    attachEventListeners();

    // 이미지 에러 처리
    document.addEventListener('error', handleImageError, true);

    isInitialized = true;

    // 기존 댓글 날짜를 상대적 시간으로 변환
    if (typeof RelativeTimeUtils !== 'undefined') {
      RelativeTimeUtils.updateAllRelativeTimes('.comment-date');
    }
  }

  /**
   * 모든 이벤트 리스너 제거
   */
  function removeAllEventListeners() {
    if (elements.submitButton) {
      const newSubmitButton = elements.submitButton.cloneNode(true);
      elements.submitButton.parentNode.replaceChild(newSubmitButton,
          elements.submitButton);
      elements.submitButton = newSubmitButton;
    }

    if (elements.cancelButton) {
      const newCancelButton = elements.cancelButton.cloneNode(true);
      elements.cancelButton.parentNode.replaceChild(newCancelButton,
          elements.cancelButton);
      elements.cancelButton = newCancelButton;
    }

    if (elements.form) {
      // form의 submit 이벤트 복제는 필요 없음 (이벤트 핸들러만 제거)
      elements.form.onsubmit = null;
    }
  }

  /**
   * 이벤트 리스너 등록
   */
  function attachEventListeners() {
    // 댓글 제출 버튼 클릭 이벤트
    if (elements.submitButton) {
      elements.submitButton.addEventListener('click', function (event) {
        event.preventDefault();
        submitComment();
      });
    }

    // 폼 제출 이벤트 (엔터키 처리)
    if (elements.form) {
      elements.form.addEventListener('submit', function (event) {
        event.preventDefault();
        submitComment();
      });
    }

    // 텍스트 영역 엔터키 처리 (input 태그용)
    if (elements.textarea) {
      elements.textarea.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
          event.preventDefault();
          submitComment();
        }
      });
    }

    // 취소 버튼 클릭 이벤트
    if (elements.cancelButton) {
      elements.cancelButton.addEventListener('click', function () {
        if (elements.textarea) {
          elements.textarea.value = '';
        }
      });
    }
  }

  /**
   * 댓글 제출 처리 함수
   */
  function submitComment() {
    // 중복 제출 방지
    if (isSubmitting) {
      return;
    }

    const content = elements.textarea.value.trim();
    if (!content) {
      window.toastManager.error('댓글 내용을 입력해주세요.');
      return;
    }

    const figureId = elements.commentList.getAttribute('data-figure-id');
    if (!figureId) {
      console.error('figure ID를 찾을 수 없습니다.');
      window.toastManager.error('잘못된 접근입니다.');
      return;
    }

    const url = `/api/v1/figures/${figureId}/comments`;

    // 로딩 상태 시작
    isSubmitting = true;
    setLoadingState(true);

    // API 호출
    CommentAPI.createComment(url, content)
    .then(function (newComment) {
      console.log('API 응답 성공:', newComment);

      if (newComment && newComment.id) {
        // UI 업데이트
        addCommentToUI(newComment);
        elements.textarea.value = '';
        updateCommentCount(1);
        hideEmptyMessages();

        window.toastManager.success('댓글이 성공적으로 등록되었습니다.');
      } else {
        console.error('API 응답에 유효한 댓글 데이터가 없습니다:', newComment);
        window.toastManager.error('댓글 등록에 실패했습니다.');
      }
    })
    .catch(function (error) {
      console.error('댓글 등록 실패:', error);
      window.toastManager.error('댓글 등록에 실패했습니다. 다시 시도해주세요.');
    })
    .finally(function () {
      // 로딩 상태 종료
      isSubmitting = false;
      setLoadingState(false);
    });
  }

  /**
   * 로딩 상태 설정
   */
  function setLoadingState(loading) {
    if (!elements.submitButton) {
      return;
    }

    if (loading) {
      elements.submitButton.disabled = true;
      elements.submitButton.innerHTML = '<i class="fas fa-spinner fa-spin mr-1"></i> 등록 중...';
      elements.submitButton.classList.add('opacity-75');
      elements.textarea.disabled = true;
    } else {
      elements.submitButton.disabled = false;
      elements.submitButton.innerHTML = '<i class="fas fa-paper-plane mr-1"></i> 댓글';
      elements.submitButton.classList.remove('opacity-75');
      elements.textarea.disabled = false;
    }
  }

  /**
   * UI에 새 댓글 추가
   */
  function addCommentToUI(commentData) {
    if (!elements.template || !elements.commentList) {
      return;
    }

    // 템플릿 복제
    const commentNode = elements.template.content.cloneNode(true);

    // 프로필 링크 설정
    const profileLink = commentNode.querySelector('a');
    if (profileLink && commentData.userNickname) {
      profileLink.href = `/users/${commentData.userNickname}`;
    }

    // 프로필 이미지 설정 - 올바른 선택자 사용
    const profileImg = commentNode.querySelector('img');
    if (profileImg) {
      profileImg.src = commentData.userProfileImage
          || '/img/profile-placeholder.svg';
      profileImg.setAttribute('data-handle-error', 'true');
      profileImg.setAttribute('data-placeholder',
          '/img/profile-placeholder.svg');
    }

    // 닉네임 설정 - 올바른 선택자 사용
    const nicknameEl = commentNode.querySelector(
        '.font-medium.text-gray-900.mr-2');
    if (nicknameEl) {
      nicknameEl.textContent = commentData.userNickname || '익명';
    }

    // 날짜 설정
    const dateEl = commentNode.querySelector(
        '.text-sm.text-gray-500.comment-date-relative');
    if (dateEl) {
      // 상대적 시간 설정
      if (typeof RelativeTimeUtils !== 'undefined') {
        RelativeTimeUtils.setRelativeTime(dateEl, commentData.createdAt);
      } else {
        dateEl.textContent = '방금 전';
        dateEl.setAttribute('data-original-date', commentData.createdAt);
        dateEl.setAttribute('title', commentData.createdAt);
      }
    }

    // 댓글 내용 설정
    const contentEl = commentNode.querySelector('.comment-content');
    if (contentEl) {
      contentEl.textContent = commentData.content;
    }

    // 좋아요/싫어요 카운트 설정
    const likeCountEl = commentNode.querySelector('.like-count');
    if (likeCountEl) {
      likeCountEl.textContent = commentData.likes || '0';
    }

    const dislikeCountEl = commentNode.querySelector('.dislike-count');
    if (dislikeCountEl) {
      dislikeCountEl.textContent = commentData.dislikes || '0';
    }

    // 댓글 ID 및 figure ID 설정
    const likeButton = commentNode.querySelector('.like-button');
    const dislikeButton = commentNode.querySelector('.dislike-button');
    const addReplyButton = commentNode.querySelector('.add-reply-button');
    const viewRepliesBtn = commentNode.querySelector('.view-replies-btn');

    const figureId = elements.commentList.getAttribute('data-figure-id');

    if (likeButton) {
      likeButton.setAttribute('data-id', commentData.id);
      likeButton.setAttribute('data-figure-id', figureId);
    }

    if (dislikeButton) {
      dislikeButton.setAttribute('data-id', commentData.id);
      dislikeButton.setAttribute('data-figure-id', figureId);
    }

    if (addReplyButton) {
      addReplyButton.setAttribute('data-id', commentData.id);
    }

    // 좋아요/싫어요 상태 클래스 설정
    if (commentData.isLikedByUser && likeButton) {
      likeButton.classList.add('clicked', 'text-indigo-600');
    }

    if (commentData.isDislikedByUser && dislikeButton) {
      dislikeButton.classList.add('clicked', 'text-red-600');
    }

    // 답글 수 설정 (새 디자인)
    if (commentData.replyCount > 0 && viewRepliesBtn) {
      viewRepliesBtn.classList.remove('hidden');
      viewRepliesBtn.setAttribute('data-id', commentData.id);
      const replyCountSpan = viewRepliesBtn.querySelector('.reply-count');
      if (replyCountSpan) {
        replyCountSpan.textContent = `답글 ${commentData.replyCount}개`;
      }
    }

    // 댓글 아이템 요소 가져오기
    const commentItem = commentNode.querySelector('.comment-item');

    // 댓글 목록 맨 위에 추가
    if (commentItem) {
      elements.commentList.insertBefore(commentItem,
          elements.commentList.firstChild);

      // 새로 추가된 댓글의 버튼들에 이벤트 리스너 등록
      // 1. 댓글 좋아요/싫어요 버튼 이벤트 등록
      if (window.CommentInteractions) {
        window.CommentInteractions.initializeAllLikeDislikeButtons(commentItem);
      }

      // 2. 답글 작성 버튼에 이벤트 등록 - 이 부분이 중요
      const addReplyBtn = commentItem.querySelector('.add-reply-button');
      if (addReplyBtn) {
        addReplyBtn.addEventListener('click', function (e) {
          // AddReplyFormHandler가 있으면 직접 메서드 호출
          if (window.AddReplyFormHandler
              && typeof window.AddReplyFormHandler.handleButtonClick
              === 'function') {
            window.AddReplyFormHandler.handleButtonClick(e);
          } else {
            // 커스텀 이벤트 발생 (이벤트 위임 방식을 사용하는 핸들러를 위해)
            const clickEvent = new MouseEvent('click', {
              bubbles: true,
              cancelable: true,
              view: window
            });
            addReplyBtn.dispatchEvent(clickEvent);
          }
        });
      }
    }
  }

  /**
   * 댓글 카운트 업데이트
   */
  function updateCommentCount(increment) {
    const countElement = document.querySelector('h2 .bg-indigo-100');
    if (countElement) {
      const currentCount = parseInt(countElement.textContent, 10) || 0;
      countElement.textContent = currentCount + increment;
    }
  }

  /**
   * 빈 댓글 메시지 숨기기
   */
  function hideEmptyMessages() {
    if (elements.noCommentsMessage) {
      elements.noCommentsMessage.classList.add('hidden');
    }
    if (elements.emptyCommentMessage) {
      elements.emptyCommentMessage.classList.add('hidden');
    }
  }

  /**
   * 이미지 오류 처리
   */
  function handleImageError(event) {
    const target = event.target;
    if (target.tagName === 'IMG' && target.hasAttribute('data-handle-error')) {
      target.src = target.getAttribute('data-placeholder')
          || '/img/profile-placeholder.svg';
    }
  }

  // 공개 API
  return {
    init: init
  };
})();

// DOM 로드 완료 시 초기화
document.addEventListener('DOMContentLoaded', function () {
  commentHandler.init();
});

// 페이지가 이미 로드되었을 경우를 대비
if (document.readyState === 'complete' || document.readyState
    === 'interactive') {
  setTimeout(function () {
    commentHandler.init();
  }, 1);
}