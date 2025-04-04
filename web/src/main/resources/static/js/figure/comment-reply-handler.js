/**
 * 댓글 및 답글 관련 기능을 처리하는 핸들러
 * 답글 토글, 답글 로드, 답글 작성 기능을 담당합니다.
 */
class CommentReplyHandler {
  constructor() {
    // DOM이 완전히 로드된 후 초기화
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', () => {
        this.initialize();
      });
    } else {
      this.initialize();
    }
  }

  /**
   * 핸들러 초기화
   */
  initialize() {
    console.log('CommentReplyHandler 초기화 시작');
    this.initializeElements();
    console.log('CommentReplyHandler 초기화 완료');
  }

  /**
   * DOM 요소 초기화
   */
  initializeElements() {
    // 답글 템플릿
    this.replyTemplate = document.getElementById('reply-template');
    console.log(`답글 템플릿 찾음: ${this.replyTemplate !== null}`);

    // 템플릿이 없으면 콘솔에 경고
    if (!this.replyTemplate) {
      console.warn('reply-template ID를 가진 템플릿 요소를 찾을 수 없습니다.');

      // HTML에서 모든 template 요소 로깅
      const allTemplates = document.querySelectorAll('template');
      console.log(`페이지의 모든 템플릿 요소: ${allTemplates.length}개`);
      allTemplates.forEach((template, index) => {
        console.log(`템플릿 ${index + 1} ID: ${template.id || '(ID 없음)'}`);
      });
    }

    // 이벤트 위임 설정
    this.setupEventDelegation();
  }

  /**
   * 이벤트 위임 설정 - 동적으로 생성되는 요소에도 이벤트 적용
   */
  setupEventDelegation() {
    // 문서 전체에 클릭 이벤트 위임
    document.addEventListener('click', (e) => {
      // 답글 토글 버튼
      const replyToggleBtn = e.target.closest('.reply-toggle-btn');
      if (replyToggleBtn) {
        this.toggleReplyForm(replyToggleBtn);
      }

      // 답글 취소 버튼
      const cancelReplyBtn = e.target.closest('.cancel-reply-btn');
      if (cancelReplyBtn) {
        this.cancelReply(cancelReplyBtn);
      }

      // 답글 보기 버튼
      const viewRepliesBtn = e.target.closest('.view-replies-btn');
      if (viewRepliesBtn) {
        this.toggleReplies(viewRepliesBtn);
      }

      // 좋아요 버튼 (동적으로 추가된 답글에 대해서도 작동)
      const replyLikeBtn = e.target.closest('.reply-like-btn');
      if (replyLikeBtn) {
        this.handleReplyInteraction(replyLikeBtn, true);
      }

      // 싫어요 버튼 (동적으로 추가된 답글에 대해서도 작동)
      const replyDislikeBtn = e.target.closest('.reply-dislike-btn');
      if (replyDislikeBtn) {
        this.handleReplyInteraction(replyDislikeBtn, false);
      }
    });
  }

  /**
   * 상대적 시간 표시 (~초 전, ~분 전 등)
   * @param {string} dateString - ISO 형식의 날짜 문자열
   * @returns {string} - 상대적 시간 문자열
   */
  formatRelativeTime(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const seconds = Math.floor((now - date) / 1000);

    if (seconds < 30) {
      return "방금";
    }
    if (seconds < 60) {
      return `${seconds}초 전`;
    }

    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) {
      return `${minutes}분 전`;
    }

    const hours = Math.floor(minutes / 60);
    if (hours < 24) {
      return `${hours}시간 전`;
    }

    const days = Math.floor(hours / 24);
    if (days < 7) {
      return `${days}일 전`;
    }

    const weeks = Math.floor(days / 7);
    if (weeks < 4) {
      return `${weeks}주일 전`;
    }

    const months = Math.floor(days / 30);
    return `${months}달 전`;
  }

  /**
   * 답글 작성 폼 토글
   * @param {HTMLElement} button - 클릭된 버튼
   */
  toggleReplyForm(button) {
    const commentId = button.dataset.commentId;
    const replyForm = document.getElementById(`reply-form-${commentId}`);

    if (replyForm) {
      // 폼이 숨겨져 있으면 표시, 아니면 숨김
      if (replyForm.style.display === 'none') {
        replyForm.style.display = 'block';
        // 텍스트 영역에 포커스
        const textarea = replyForm.querySelector('textarea');
        if (textarea) {
          textarea.focus();
        }
      } else {
        replyForm.style.display = 'none';
      }
    }
  }

  /**
   * 답글 작성 취소
   * @param {HTMLElement} button - 클릭된 버튼
   */
  cancelReply(button) {
    const commentId = button.dataset.commentId;
    const replyForm = document.getElementById(`reply-form-${commentId}`);

    if (replyForm) {
      // 폼 숨기고 내용 초기화
      replyForm.style.display = 'none';
      const textarea = replyForm.querySelector('textarea');
      if (textarea) {
        textarea.value = '';
      }
    }
  }

  /**
   * 답글 목록 토글 및 로드
   * @param {HTMLElement} button - 클릭된 버튼
   */
  toggleReplies(button) {
    const commentId = button.dataset.commentId;
    const repliesContainer = document.getElementById(`replies-${commentId}`);

    console.log(`토글 답글: 댓글 ID ${commentId}`); // 디버깅 로그

    if (repliesContainer) {
      // 컨테이너가 표시되어 있으면 숨기고, 숨겨져 있으면 표시
      if (repliesContainer.style.display === 'none') {
        repliesContainer.style.display = 'block';

        // 아이콘 변경
        const icon = button.querySelector('i');
        if (icon) {
          icon.classList.remove('fa-chevron-down');
          icon.classList.add('fa-chevron-up');
        }

        // 답글이 이미 로드되었는지 확인
        const repliesLoaded = repliesContainer.getAttribute('data-loaded')
            === 'true';

        if (!repliesLoaded) {
          this.loadReplies(commentId, repliesContainer);
        }
      } else {
        repliesContainer.style.display = 'none';

        // 아이콘 변경
        const icon = button.querySelector('i');
        if (icon) {
          icon.classList.remove('fa-chevron-up');
          icon.classList.add('fa-chevron-down');
        }
      }
    }
  }

  /**
   * 답글 목록 로드
   * @param {string} commentId - 댓글 ID
   * @param {HTMLElement} container - 답글 컨테이너 요소
   */
  loadReplies(commentId, container) {
    console.log(`답글 로드 시작: 댓글 ID ${commentId}`); // 디버깅 로그

    // 로딩 상태 표시
    const loadingElement = container.querySelector('.replies-loading');
    if (loadingElement) {
      loadingElement.style.display = 'block';
    }

    // 답글 API 호출
    fetch(`/api/comments/${commentId}/replies`)
    .then(response => {
      console.log(`응답 상태: ${response.status}`); // 디버깅 로그
      if (!response.ok) {
        throw new Error('답글을 불러오는데 실패했습니다.');
      }
      return response.json();
    })
    .then(replies => {
      console.log(`받은 답글 수: ${replies.length}`); // 디버깅 로그

      // 로딩 상태 숨김
      if (loadingElement) {
        loadingElement.style.display = 'none';
      }

      // 답글이 없는 경우
      if (replies.length === 0) {
        const emptyMessage = document.createElement('div');
        emptyMessage.className = 'text-center py-4 text-gray-500';
        emptyMessage.innerHTML = '<p>아직 답글이 없습니다.</p>';
        container.appendChild(emptyMessage);
      } else {
        // 템플릿이 없는 경우 직접 DOM 생성
        if (!this.replyTemplate) {
          console.log('템플릿이 없어 직접 DOM으로 답글 생성');
          this.renderRepliesWithoutTemplate(replies, container);
        } else {
          // 템플릿을 사용하여 답글 렌더링
          replies.forEach(reply => this.renderReply(reply, container));
        }
      }

      // 로드 완료 표시
      container.setAttribute('data-loaded', 'true');
    })
    .catch(error => {
      console.error('답글 로드 오류:', error);

      // 로딩 상태 숨김
      if (loadingElement) {
        loadingElement.style.display = 'none';
      }

      // 오류 메시지 표시
      const errorMessage = document.createElement('div');
      errorMessage.className = 'text-center py-4 text-red-500';
      errorMessage.innerHTML = `<p><i class="fas fa-exclamation-circle mr-1"></i>답글을 불러오는데 실패했습니다.</p>`;
      container.appendChild(errorMessage);

      // 토스트 메시지 표시
      if (window.ToastManager) {
        window.ToastManager.show('답글을 불러오는데 실패했습니다.', true);
      }
    });
  }

  /**
   * 템플릿 없이 답글 렌더링 (백업 방법)
   * @param {Array} replies - 답글 데이터 배열
   * @param {HTMLElement} container - 답글 컨테이너 요소
   */
  renderRepliesWithoutTemplate(replies, container) {
    replies.forEach(reply => {
      // 답글 요소 생성
      const replyItem = document.createElement('div');
      replyItem.className = 'reply-item pb-4 mb-4 border-b border-gray-100';
      replyItem.dataset.replyId = reply.id;

      // 메타 정보 (프로필 사진과 사용자 정보 포함)
      const metaDiv = document.createElement('div');
      metaDiv.className = 'flex justify-between mb-1';

      const userInfoDiv = document.createElement('div');
      userInfoDiv.className = 'flex items-center';

      // 프로필 이미지 추가
      const profileLink = document.createElement('a');
      profileLink.href = reply.userNickname ? `/users/${reply.userNickname}`
          : '#';

      const profileImg = document.createElement('img');
      profileImg.src = reply.userProfileImage || '/img/profile-placeholder.svg';
      profileImg.alt = '프로필 이미지';
      profileImg.className = 'w-8 h-8 rounded-full mr-2';
      profileImg.onerror = "this.src='/img/profile-placeholder.svg'";

      profileLink.appendChild(profileImg);
      userInfoDiv.appendChild(profileLink);

      // 사용자 정보 (닉네임 + 날짜)
      const userTextDiv = document.createElement('div');
      userTextDiv.className = 'text-sm';

      // 닉네임 링크
      const authorLink = document.createElement('a');
      authorLink.href = reply.userNickname ? `/users/${reply.userNickname}`
          : '#';
      authorLink.className = 'font-medium text-gray-900 hover:text-indigo-600 transition';

      const authorSpan = document.createElement('span');
      authorSpan.textContent = reply.userNickname || '익명';

      authorLink.appendChild(authorSpan);
      userTextDiv.appendChild(authorLink);

      // 날짜 추가 - 상대적 시간 표시로 변경
      const dateSpan = document.createElement('span');
      dateSpan.className = 'ml-3 text-gray-500';
      dateSpan.textContent = this.formatRelativeTime(reply.createdAt);

      userTextDiv.appendChild(dateSpan);
      userInfoDiv.appendChild(userTextDiv);
      metaDiv.appendChild(userInfoDiv);
      replyItem.appendChild(metaDiv);

      // 내용
      const content = document.createElement('p');
      content.className = 'text-gray-800 mb-2 pl-10';
      content.textContent = reply.content;
      replyItem.appendChild(content);

      // 액션 버튼
      const actionsDiv = document.createElement('div');
      actionsDiv.className = 'flex items-center text-sm pl-10';

      // 좋아요 버튼
      const likeBtn = document.createElement('button');
      likeBtn.className = 'flex items-center mr-4 text-gray-500 hover:text-green-500 transition reply-like-btn';
      likeBtn.innerHTML = `<i class="fas fa-thumbs-up mr-1"></i><span>${reply.likes}</span>`;
      likeBtn.dataset.replyId = reply.id;

      // 싫어요 버튼
      const dislikeBtn = document.createElement('button');
      dislikeBtn.className = 'flex items-center text-gray-500 hover:text-red-500 transition reply-dislike-btn';
      dislikeBtn.innerHTML = `<i class="fas fa-thumbs-down mr-1"></i><span>${reply.dislikes}</span>`;
      dislikeBtn.dataset.replyId = reply.id;

      actionsDiv.appendChild(likeBtn);
      actionsDiv.appendChild(dislikeBtn);
      replyItem.appendChild(actionsDiv);

      // 컨테이너에 추가
      container.appendChild(replyItem);
    });
  }

  /**
   * 답글 렌더링
   * @param {Object} reply - 답글 데이터
   * @param {HTMLElement} container - 답글 컨테이너 요소
   */
  renderReply(reply, container) {
    if (!this.replyTemplate) {
      console.error('답글 템플릿을 찾을 수 없습니다.');
      return;
    }

    // 템플릿 복제
    const replyNode = document.importNode(this.replyTemplate.content, true);
    const replyElement = replyNode.querySelector('.reply-item');

    // 데이터 바인딩
    replyElement.dataset.replyId = reply.id;
    replyElement.querySelector('.reply-content').textContent = reply.content;

    // 상대적 시간 표시로 변경
    replyElement.querySelector(
        '.reply-date').textContent = this.formatRelativeTime(reply.createdAt);

    replyElement.querySelector('.reply-likes').textContent = reply.likes;
    replyElement.querySelector('.reply-dislikes').textContent = reply.dislikes;

    // 사용자 정보 바인딩
    if (reply.userNickname) {
      replyElement.querySelector(
          '.reply-author').textContent = reply.userNickname;

      // 사용자 프로필 링크 설정
      const profileLink = `/users/${reply.userNickname}`;
      replyElement.querySelector('.user-profile-link').href = profileLink;
      replyElement.querySelector('.reply-author-link').href = profileLink;

      // 프로필 이미지 설정
      if (reply.userProfileImage) {
        replyElement.querySelector(
            '.reply-profile-image').src = reply.userProfileImage;
      }
    } else {
      replyElement.querySelector('.reply-author').textContent = '익명';
    }

    // 좋아요/싫어요 버튼에 데이터 속성 추가
    const likeButton = replyElement.querySelector('.reply-like-btn');
    const dislikeButton = replyElement.querySelector('.reply-dislike-btn');

    if (likeButton) {
      likeButton.dataset.replyId = reply.id;
    }

    if (dislikeButton) {
      dislikeButton.dataset.replyId = reply.id;
    }

    // 컨테이너에 추가
    container.appendChild(replyNode);
  }

  /**
   * 답글 상호작용(좋아요/싫어요) 처리
   * @param {HTMLElement} button - 클릭된 버튼
   * @param {boolean} isLike - 좋아요(true) 또는 싫어요(false)
   */
  handleReplyInteraction(button, isLike) {
    const replyId = button.dataset.replyId;
    const actionEndpoint = isLike ? 'like' : 'dislike';
    const countElement = button.querySelector('span');

    fetch(`/api/comments/${replyId}/${actionEndpoint}`, {
      method: 'POST'
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('상호작용 등록에 실패했습니다.');
      }
      return response.json();
    })
    .then(data => {
      // 카운트 업데이트
      countElement.textContent = isLike ? data.likes : data.dislikes;

      // 성공 메시지 표시
      if (window.ToastManager) {
        window.ToastManager.show(isLike ? '좋아요를 표시했습니다.' : '싫어요를 표시했습니다.');
      }
    })
    .catch(error => {
      console.error('상호작용 등록 실패:', error);
      if (window.ToastManager) {
        window.ToastManager.show(error.message, true);
      }
    });
  }
}

// 전역 객체로 내보내기
window.CommentReplyHandler = CommentReplyHandler;