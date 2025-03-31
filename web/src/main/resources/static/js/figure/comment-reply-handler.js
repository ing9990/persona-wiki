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
    this.addEventListeners();
    console.log('CommentReplyHandler 초기화 완료');
  }

  /**
   * DOM 요소 초기화
   */
  initializeElements() {
    // 답글 토글 버튼
    this.replyToggleButtons = document.querySelectorAll('.reply-toggle-btn');
    console.log(`답글 토글 버튼 수: ${this.replyToggleButtons.length}`);

    // 답글 취소 버튼
    this.cancelReplyButtons = document.querySelectorAll('.cancel-reply-btn');
    console.log(`답글 취소 버튼 수: ${this.cancelReplyButtons.length}`);

    // 답글 보기 버튼
    this.viewRepliesButtons = document.querySelectorAll('.view-replies-btn');
    console.log(`답글 보기 버튼 수: ${this.viewRepliesButtons.length}`);

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
  }

  /**
   * 이벤트 리스너 추가
   */
  addEventListeners() {
    // 답글 작성 폼 토글 이벤트
    this.replyToggleButtons.forEach(button => {
      button.addEventListener('click', this.toggleReplyForm.bind(this));
    });

    // 답글 취소 버튼 이벤트
    this.cancelReplyButtons.forEach(button => {
      button.addEventListener('click', this.cancelReply.bind(this));
    });

    // 답글 보기 버튼 이벤트
    this.viewRepliesButtons.forEach(button => {
      button.addEventListener('click', this.toggleReplies.bind(this));
      console.log(`답글 보기 버튼 이벤트 리스너 추가: ${button.textContent.trim()}`);
    });
  }

  /**
   * 답글 작성 폼 토글
   * @param {Event} event - 클릭 이벤트
   */
  toggleReplyForm(event) {
    const commentId = event.currentTarget.dataset.commentId;
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
   * @param {Event} event - 클릭 이벤트
   */
  cancelReply(event) {
    const commentId = event.currentTarget.dataset.commentId;
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
   * @param {Event} event - 클릭 이벤트
   */
  toggleReplies(event) {
    const button = event.currentTarget;
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

      // 메타 정보
      const metaDiv = document.createElement('div');
      metaDiv.className = 'flex justify-between mb-1';

      const authorDiv = document.createElement('div');
      authorDiv.className = 'text-sm text-gray-500';
      authorDiv.innerHTML = `
        <i class="fas fa-user-circle mr-1"></i>
        <span>익명</span>
        <span class="ml-3">${new Date(reply.createdAt).toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })}</span>
      `;

      metaDiv.appendChild(authorDiv);
      replyItem.appendChild(metaDiv);

      // 내용
      const content = document.createElement('p');
      content.className = 'text-gray-800 mb-2';
      content.textContent = reply.content;
      replyItem.appendChild(content);

      // 액션 버튼
      const actionsDiv = document.createElement('div');
      actionsDiv.className = 'flex items-center text-sm';

      // 좋아요 버튼
      const likeBtn = document.createElement('button');
      likeBtn.className = 'flex items-center mr-4 text-gray-500 hover:text-green-500 transition';
      likeBtn.innerHTML = `<i class="fas fa-thumbs-up mr-1"></i><span>${reply.likes}</span>`;
      likeBtn.dataset.replyId = reply.id;
      likeBtn.addEventListener('click', this.handleReplyLike.bind(this));

      // 싫어요 버튼
      const dislikeBtn = document.createElement('button');
      dislikeBtn.className = 'flex items-center text-gray-500 hover:text-red-500 transition';
      dislikeBtn.innerHTML = `<i class="fas fa-thumbs-down mr-1"></i><span>${reply.dislikes}</span>`;
      dislikeBtn.dataset.replyId = reply.id;
      dislikeBtn.addEventListener('click', this.handleReplyDislike.bind(this));

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
    replyElement.querySelector('.reply-date').textContent =
        new Date(reply.createdAt).toLocaleString('ko-KR', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
    replyElement.querySelector('.reply-likes').textContent = reply.likes;
    replyElement.querySelector('.reply-dislikes').textContent = reply.dislikes;

    // 좋아요/싫어요 버튼 이벤트 연결
    const likeButton = replyElement.querySelector('.reply-like-btn');
    const dislikeButton = replyElement.querySelector('.reply-dislike-btn');

    if (likeButton) {
      likeButton.dataset.replyId = reply.id;
      likeButton.addEventListener('click', this.handleReplyLike.bind(this));
    }

    if (dislikeButton) {
      dislikeButton.dataset.replyId = reply.id;
      dislikeButton.addEventListener('click',
          this.handleReplyDislike.bind(this));
    }

    // 대댓글 버튼 이벤트 연결 (필요한 경우)
    const replyToReplyBtn = replyElement.querySelector('.reply-to-reply-btn');
    if (replyToReplyBtn) {
      replyToReplyBtn.dataset.replyId = reply.id;
      replyToReplyBtn.addEventListener('click',
          this.toggleNestedReplyForm.bind(this));
    }

    // 컨테이너에 추가
    container.appendChild(replyNode);
  }

  /**
   * 답글 좋아요 처리
   * @param {Event} event - 클릭 이벤트
   */
  handleReplyLike(event) {
    const replyId = event.currentTarget.dataset.replyId;
    this.handleReplyInteraction(replyId, true, event.currentTarget);
  }

  /**
   * 답글 싫어요 처리
   * @param {Event} event - 클릭 이벤트
   */
  handleReplyDislike(event) {
    const replyId = event.currentTarget.dataset.replyId;
    this.handleReplyInteraction(replyId, false, event.currentTarget);
  }

  /**
   * 답글 상호작용(좋아요/싫어요) 처리
   * @param {string} replyId - 답글 ID
   * @param {boolean} isLike - 좋아요(true) 또는 싫어요(false)
   * @param {HTMLElement} button - 클릭된 버튼
   */
  handleReplyInteraction(replyId, isLike, button) {
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
        window.ToastManager.show(
            isLike ? '좋아요를 표시했습니다.' : '싫어요를 표시했습니다.'
        );
      }
    })
    .catch(error => {
      console.error('상호작용 등록 실패:', error);
      if (window.ToastManager) {
        window.ToastManager.show(error.message, true);
      }
    });
  }

  /**
   * 대댓글 폼 토글
   * @param {Event} event - 클릭 이벤트
   */
  toggleNestedReplyForm(event) {
    const replyElement = event.currentTarget.closest('.reply-item');
    const nestedForm = replyElement.querySelector('.reply-to-reply-form');

    if (nestedForm) {
      nestedForm.classList.toggle('hidden');

      // 폼이 표시되면 텍스트 영역에 포커스
      if (!nestedForm.classList.contains('hidden')) {
        const textarea = nestedForm.querySelector('textarea');
        if (textarea) {
          textarea.focus();
        }
      }
    }
  }
}