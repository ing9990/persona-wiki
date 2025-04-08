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
    textarea: null,
    submitButton: null,
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

    console.log('댓글 핸들러 초기화 시작');

    // DOM 요소 캐시
    elements.form = document.getElementById('comment-form');
    elements.textarea = document.getElementById('comment-textarea');
    elements.submitButton = document.getElementById('comment-submit-button');
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

    // 모든 기존 이벤트 리스너 제거를 위한 복제 요소로 교체
    var oldButton = elements.submitButton;
    var newButton = oldButton.cloneNode(true);
    oldButton.parentNode.replaceChild(newButton, oldButton);
    elements.submitButton = newButton;

    // 새 이벤트 리스너 등록
    elements.submitButton.addEventListener('click', submitComment);

    // 이미지 에러 처리
    document.addEventListener('error', handleImageError, true);

    isInitialized = true;
    console.log('댓글 핸들러 초기화 완료');
  }

  /**
   * 댓글 제출 처리 함수
   */
  function submitComment(event) {
    // 이벤트 기본 동작 방지
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    // 중복 제출 방지
    if (isSubmitting) {
      console.log('이미 제출 중입니다.');
      return;
    }

    console.log('댓글 제출 시작');

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
      elements.submitButton.classList.add('opacity-75', 'cursor-not-allowed');
      elements.textarea.disabled = true;
    } else {
      elements.submitButton.disabled = false;
      elements.submitButton.innerHTML = '<i class="fas fa-paper-plane mr-1"></i> 게시하기';
      elements.submitButton.classList.remove('opacity-75',
          'cursor-not-allowed');
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

    // 프로필 이미지 설정
    const profileImg = commentNode.querySelector('.user-profile');
    if (profileImg) {
      profileImg.src = commentData.userProfileImage
          || '/img/profile-placeholder.svg';
      profileImg.setAttribute('data-handle-error', 'true');
      profileImg.setAttribute('data-placeholder',
          '/img/profile-placeholder.svg');
    }

    // 닉네임 및 날짜 설정
    const nicknameEl = commentNode.querySelector('.user-nickname');
    if (nicknameEl) {
      nicknameEl.textContent = commentData.userNickname || '익명';
    }

    const dateEl = commentNode.querySelector('.comment-date');
    if (dateEl) {
      dateEl.textContent = commentData.createdAt;
    }

    // 댓글 내용 설정
    const contentEl = commentNode.querySelector('.comment-content');
    if (contentEl) {
      contentEl.textContent = commentData.content;
    }

    // 좋아요/싫어요 카운트 설정
    const likeCountEl = commentNode.querySelector('.like-count');
    if (likeCountEl) {
      likeCountEl.textContent = commentData.likes;
    }

    const dislikeCountEl = commentNode.querySelector('.dislike-count');
    if (dislikeCountEl) {
      dislikeCountEl.textContent = commentData.dislikes;
    }

    // 댓글 ID 및 figure ID 설정
    const likeButton = commentNode.querySelector('.like-button');
    const dislikeButton = commentNode.querySelector('.dislike-button');
    const replyButton = commentNode.querySelector('.reply-button');

    const figureId = elements.commentList.getAttribute('data-figure-id');

    if (likeButton) {
      likeButton.setAttribute('data-id', commentData.id);
      likeButton.setAttribute('data-figure-id', figureId);
    }

    if (dislikeButton) {
      dislikeButton.setAttribute('data-id', commentData.id);
      dislikeButton.setAttribute('data-figure-id', figureId);
    }

    if (replyButton) {
      replyButton.setAttribute('data-id', commentData.id);
    }

    // 좋아요/싫어요 상태 클래스 설정
    if (commentData.isLikedByUser && likeButton) {
      likeButton.classList.add('clicked', 'text-indigo-600');
    }

    if (commentData.isDislikedByUser && dislikeButton) {
      dislikeButton.classList.add('clicked', 'text-red-600');
    }

    // 답글 수 설정
    if (replyButton) {
      const replyCountSpan = replyButton.querySelector('.reply-count');
      if (replyCountSpan) {
        replyCountSpan.textContent = commentData.replyCount > 0
            ? `(${commentData.replyCount})` : '';
      }
    }

    // 댓글 목록 맨 위에 추가
    const commentItem = commentNode.querySelector('.comment-item');
    if (commentItem) {
      elements.commentList.insertBefore(commentItem,
          elements.commentList.firstChild);
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
  console.log('DOMContentLoaded 이벤트 발생');
  commentHandler.init();
});

// 페이지가 이미 로드되었을 경우를 대비
if (document.readyState === 'complete' || document.readyState
    === 'interactive') {
  console.log('페이지가 이미 로드됨, 즉시 초기화');
  setTimeout(function () {
    commentHandler.init();
  }, 1);
}