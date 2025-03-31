// web/src/main/resources/static/js/figure/comment-reply-handler.js

/**
 * 댓글 및 답글 관련 기능을 처리하는 핸들러
 */
class CommentReplyHandler {
  constructor() {
    this.initializeElements();
    this.bindEvents();
  }

  /**
   * DOM 요소 초기화
   */
  initializeElements() {
    // 답글 토글 버튼
    this.replyToggleButtons = document.querySelectorAll('.reply-toggle-btn');
    this.cancelReplyButtons = document.querySelectorAll('.cancel-reply-btn');

    // 답글 보기 버튼
    this.viewRepliesButtons = document.querySelectorAll('.view-replies-btn');

    // 댓글 좋아요/싫어요 버튼 (기존 기능 연결)
    this.commentLikeButtons = document.querySelectorAll(
        '[data-action="like-comment"]');
    this.commentDislikeButtons = document.querySelectorAll(
        '[data-action="dislike-comment"]');

    // 답글 템플릿
    this.replyTemplate = document.getElementById('reply-template');
  }

  /**
   * 이벤트 바인딩
   */
  bindEvents() {
    // 답글 작성 폼 토글
    this.replyToggleButtons.forEach(button => {
      button.addEventListener('click', this.toggleReplyForm.bind(this));
    });

    // 답글 작성 취소
    this.cancelReplyButtons.forEach(button => {
      button.addEventListener('click', this.hideReplyForm.bind(this));
    });

    // 답글 보기/숨기기
    this.viewRepliesButtons.forEach(button => {
      button.addEventListener('click', this.toggleReplies.bind(this));
    });

    // 댓글 좋아요/싫어요 (기존 기능 연결)
    this.commentLikeButtons.forEach(button => {
      button.addEventListener('click',
          this.handleCommentInteraction.bind(this, true));
    });

    this.commentDislikeButtons.forEach(button => {
      button.addEventListener('click',
          this.handleCommentInteraction.bind(this, false));
    });

    // 답글 폼 제출 이벤트는 HTML의 form action으로 처리
  }

  /**
   * 답글 작성 폼을 토글합니다
   * @param {Event} event - 클릭 이벤트
   */
  toggleReplyForm(event) {
    const commentId = event.currentTarget.getAttribute('data-comment-id');
    const replyForm = document.getElementById(`reply-form-${commentId}`);

    // 모든 답글 폼을 먼저 숨김
    document.querySelectorAll('.reply-form').forEach(form => {
      if (form.id !== `reply-form-${commentId}`) {
        form.classList.add('hidden');
      }
    });

    // 선택한 답글 폼 토글
    replyForm.classList.toggle('hidden');

    // 폼이 표시되면 포커스
    if (!replyForm.classList.contains('hidden')) {
      replyForm.querySelector('textarea').focus();
    }
  }

  /**
   * 답글 작성 폼을 숨깁니다
   * @param {Event} event - 클릭 이벤트
   */
  hideReplyForm(event) {
    const commentId = event.currentTarget.getAttribute('data-comment-id');
    const replyForm = document.getElementById(`reply-form-${commentId}`);
    replyForm.classList.add('hidden');
  }

  /**
   * 답글 목록을 토글(보기/숨기기)합니다
   * @param {Event} event - 클릭 이벤트
   */
  toggleReplies(event) {
    const button = event.currentTarget;
    const commentId = button.getAttribute('data-comment-id');
    const repliesContainer = document.getElementById(`replies-${commentId}`);

    // 컨테이너가 숨겨져 있으면 답글 로드, 아니면 숨김
    if (repliesContainer.classList.contains('hidden')) {
      // 컨테이너 표시
      repliesContainer.classList.remove('hidden');

      // 아직 로드되지 않았으면 답글 로드
      if (!repliesContainer.getAttribute('data-loaded')) {
        this.loadReplies(commentId, repliesContainer);
        button.innerHTML = '<i class="fas fa-chevron-up mr-1"></i> 답글 숨기기';
      }
    } else {
      // 컨테이너 숨김
      repliesContainer.classList.add('hidden');
      button.innerHTML = '<i class="fas fa-comments mr-1"></i> 답글 보기(' +
          button.getAttribute('data-reply-count') + '개)';
    }
  }

  /**
   * 답글 목록을 로드합니다
   * @param {string} commentId - 댓글 ID
   * @param {HTMLElement} container - 답글을 표시할 컨테이너
   */
  loadReplies(commentId, container) {
    // 로딩 표시
    container.querySelector('.replies-loading').classList.remove('hidden');

    // API 호출하여 답글 로드
    fetch(`/api/comments/${commentId}/replies`)
    .then(response => {
      if (!response.ok) {
        throw new Error('답글을 불러오는데 실패했습니다.');
      }
      return response.json();
    })
    .then(replies => {
      // 로딩 표시 제거
      container.querySelector('.replies-loading').classList.add('hidden');

      // 컨테이너 비우기 (로딩 표시 제외)
      Array.from(container.children).forEach(child => {
        if (!child.classList.contains('replies-loading')) {
          container.removeChild(child);
        }
      });

      // 답글이 없는 경우
      if (replies.length === 0) {
        const emptyMessage = document.createElement('div');
        emptyMessage.className = 'text-center py-2 text-gray-500';
        emptyMessage.textContent = '아직 답글이 없습니다.';
        container.appendChild(emptyMessage);
      } else {
        // 답글 추가
        replies.forEach(reply => {
          const replyElement = this.createReplyElement(reply);
          container.appendChild(replyElement);
        });
      }

      // 로드 완료 표시
      container.setAttribute('data-loaded', 'true');
    })
    .catch(error => {
      console.error('Error:', error);
      container.querySelector('.replies-loading').classList.add('hidden');

      const errorMessage = document.createElement('div');
      errorMessage.className = 'text-center py-2 text-red-500';
      errorMessage.textContent = '답글을 불러오는데 실패했습니다.';
      container.appendChild(errorMessage);
    });
  }

  /**
   * 답글 요소를 생성합니다
   * @param {Object} reply - 답글 데이터
   * @returns {HTMLElement} - 생성된 답글 요소
   */
  createReplyElement(reply) {
    // 템플릿 복제
    const replyNode = document.importNode(this.replyTemplate.content, true);
    const replyElement = replyNode.querySelector('.reply-item');

    // 데이터 채우기
    replyElement.setAttribute('data-reply-id', reply.id);
    replyElement.querySelector('.reply-date').textContent = this.formatDate(
        reply.createdAt);
    replyElement.querySelector('.reply-content').textContent = reply.content;
    replyElement.querySelector('.reply-likes').textContent = reply.likes;
    replyElement.querySelector('.reply-dislikes').textContent = reply.dislikes;

    // 좋아요/싫어요 버튼 이벤트
    const likeButton = replyElement.querySelector('.reply-like-btn');
    const dislikeButton = replyElement.querySelector('.reply-dislike-btn');

    likeButton.setAttribute('data-reply-id', reply.id);
    dislikeButton.setAttribute('data-reply-id', reply.id);

    likeButton.addEventListener('click',
        () => this.handleReplyInteraction(reply.id, true));
    dislikeButton.addEventListener('click',
        () => this.handleReplyInteraction(reply.id, false));

    // 대댓글 관련 이벤트
    const replyToReplyBtn = replyElement.querySelector('.reply-to-reply-btn');
    const cancelNestedReplyBtn = replyElement.querySelector(
        '.cancel-nested-reply-btn');
    const nestedReplyForm = replyElement.querySelector('.nested-reply-form');

    replyToReplyBtn.addEventListener('click', () => {
      const replyToReplyForm = replyElement.querySelector(
          '.reply-to-reply-form');
      replyToReplyForm.classList.toggle('hidden');

      if (!replyToReplyForm.classList.contains('hidden')) {
        replyToReplyForm.querySelector('textarea').focus();
      }
    });

    cancelNestedReplyBtn.addEventListener('click', () => {
      replyElement.querySelector('.reply-to-reply-form').classList.add(
          'hidden');
    });

    nestedReplyForm.addEventListener('submit', (e) => {
      e.preventDefault();
      this.submitNestedReply(reply.id, nestedReplyForm);
    });

    // 들여쓰기 레벨에 따른 스타일 적용
    if (reply.depth > 1) {
      replyElement.classList.add(`pl-${reply.depth * 2}`);
    }

    return replyElement;
  }

  /**
   * 대댓글을 제출합니다
   * @param {string} parentReplyId - 부모 답글 ID
   * @param {HTMLFormElement} form - 폼 요소
   */
  submitNestedReply(parentReplyId, form) {
    const content = form.querySelector('textarea').value.trim();

    if (!content) {
      // 빈 내용이면 중단
      return;
    }

    // API 호출하여 대댓글 추가
    fetch(`/api/comments/${parentReplyId}/replies`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({content})
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('답글을 작성하는데 실패했습니다.');
      }
      return response.json();
    })
    .then(nestedReply => {
      // 성공 메시지 표시
      window.ToastManager.show('답글이 등록되었습니다.');

      // 폼 초기화 및 숨김
      form.reset();
      form.closest('.reply-to-reply-form').classList.add('hidden');

      // 필요시 부모 댓글의 답글 컨테이너 새로고침
      const rootCommentId = this.findRootCommentId(parentReplyId);
      if (rootCommentId) {
        const repliesContainer = document.getElementById(
            `replies-${rootCommentId}`);
        if (repliesContainer) {
          repliesContainer.removeAttribute('data-loaded');
          this.loadReplies(rootCommentId, repliesContainer);
        }
      }
    })
    .catch(error => {
      console.error('Error:', error);
      window.ToastManager.show('답글 등록에 실패했습니다.', true);
    });
  }

  /**
   * 루트 댓글 ID를 찾습니다
   * @param {string} replyId - 답글 ID
   * @returns {string|null} - 루트 댓글 ID 또는 null
   */
  findRootCommentId(replyId) {
    // 여기서는 DOM 구조를 통해 루트 댓글 ID를 찾는 간단한 방법 사용
    // 실제로는 API 응답에 rootId가 포함되어야 함
    const replyElement = document.querySelector(`[data-reply-id="${replyId}"]`);
    if (!replyElement) {
      return null;
    }

    const container = replyElement.closest('.replies-container');
    if (!container) {
      return null;
    }

    const idMatch = container.id.match(/replies-(\d+)/);
    return idMatch ? idMatch[1] : null;
  }

  /**
   * 날짜를 포맷팅합니다
   * @param {string} dateString - ISO 형식의 날짜 문자열
   * @returns {string} - 포맷팅된 날짜
   */
  formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * 댓글 좋아요/싫어요 처리 (기존 메서드 유지)
   * @param {boolean} isLike - 좋아요(true) 또는 싫어요(false)
   * @param {Event} event - 클릭 이벤트
   */
  handleCommentInteraction(isLike, event) {
    const commentId = event.currentTarget.dataset.commentId;
    const actionEndpoint = isLike ? 'like' : 'dislike';
    const countElement = event.currentTarget.querySelector(
        '.like-count, .dislike-count');

    fetch(`/api/comments/${commentId}/${actionEndpoint}`, {
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
      window.ToastManager.show(isLike ? '좋아요를 표시했습니다.' : '싫어요를 표시했습니다.');
    })
    .catch(error => {
      console.error('상호작용 등록 실패:', error);
      window.ToastManager.show(error.message, true);
    });
  }

  /**
   * 답글 좋아요/싫어요 처리
   * @param {string} replyId - 답글 ID
   * @param {boolean} isLike - 좋아요(true) 또는 싫어요(false)
   */
  handleReplyInteraction(replyId, isLike) {
    const actionEndpoint = isLike ? 'like' : 'dislike';

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
      // DOM에서 해당 답글 찾기
      const replyElement = document.querySelector(
          `[data-reply-id="${replyId}"]`);
      if (replyElement) {
        // 카운트 업데이트
        const countElement = replyElement.querySelector(
            isLike ? '.reply-likes' : '.reply-dislikes'
        );
        countElement.textContent = isLike ? data.likes : data.dislikes;
      }

      // 성공 메시지 표시
      window.ToastManager.show(isLike ? '좋아요를 표시했습니다.' : '싫어요를 표시했습니다.');
    })
    .catch(error => {
      console.error('상호작용 등록 실패:', error);
      window.ToastManager.show(error.message, true);
    });
  }

  // web/src/main/resources/static/js/figure/comment-reply-handler.js에 추가할 코드

  /**
   * 멘션 기능을 초기화합니다
   */
  initMentionFeature() {
    // 댓글 작성 영역에 키 이벤트 리스너 추가
    document.querySelectorAll('textarea').forEach(textarea => {
      textarea.addEventListener('input', this.handleMentionInput.bind(this));
      textarea.addEventListener('keydown',
          this.handleMentionKeydown.bind(this));
    });

    // 멘션 선택 UI 생성
    const mentionMenu = document.createElement('div');
    mentionMenu.id = 'mention-menu';
    mentionMenu.className = 'absolute z-50 bg-white rounded-md shadow-lg border border-gray-200 hidden';
    mentionMenu.style.minWidth = '200px';
    document.body.appendChild(mentionMenu);

    this.mentionMenu = mentionMenu;
    this.mentionQuery = '';
    this.mentionTarget = null;
    this.selectedMentionIndex = -1;
  }

  /**
   * 멘션 입력 처리
   * @param {Event} e - 입력 이벤트
   */
  handleMentionInput(e) {
    const textarea = e.target;
    const text = textarea.value;
    const cursorPos = textarea.selectionStart;

    // '@' 문자 검색
    const lastAtIndex = text.lastIndexOf('@', cursorPos - 1);

    if (lastAtIndex >= 0 && lastAtIndex < cursorPos) {
      // '@' 문자와 커서 사이의 텍스트 (멘션 쿼리)
      const query = text.substring(lastAtIndex + 1, cursorPos);

      // 공백이 없으면 멘션 처리
      if (!query.includes(' ') && query.length > 0) {
        this.mentionQuery = query;
        this.mentionTarget = textarea;
        this.selectedMentionIndex = -1;
        this.showMentionMenu(textarea, query);
        return;
      }
    }

    // 멘션 조건에 맞지 않으면 메뉴 숨김
    this.hideMentionMenu();
  }

  /**
   * 멘션 키보드 처리
   * @param {KeyboardEvent} e - 키보드 이벤트
   */
  handleMentionKeydown(e) {
    if (!this.mentionMenu || this.mentionMenu.classList.contains('hidden')) {
      return;
    }

    const items = this.mentionMenu.querySelectorAll('.mention-item');

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        this.selectedMentionIndex = Math.min(this.selectedMentionIndex + 1,
            items.length - 1);
        this.highlightSelectedMention();
        break;

      case 'ArrowUp':
        e.preventDefault();
        this.selectedMentionIndex = Math.max(this.selectedMentionIndex - 1, -1);
        this.highlightSelectedMention();
        break;

      case 'Enter':
        if (this.selectedMentionIndex >= 0 && this.selectedMentionIndex
            < items.length) {
          e.preventDefault();
          this.selectMention(items[this.selectedMentionIndex]);
        }
        break;

      case 'Escape':
        e.preventDefault();
        this.hideMentionMenu();
        break;
    }
  }

  /**
   * 멘션 메뉴 표시
   * @param {HTMLTextAreaElement} textarea - 텍스트 영역
   * @param {string} query - 검색 쿼리
   */
  showMentionMenu(textarea, query) {
    // 댓글 작성자 목록 검색 (여기서는 간단하게 고정 목록 사용)
    // 실제로는 API 호출하여 해당 댓글 스레드의 작성자 목록 가져와야 함
    const commenters = ['익명', '다른 사용자', '관리자'];

    // 쿼리와 일치하는 사용자 필터링
    const filtered = commenters.filter(name =>
        name.toLowerCase().includes(query.toLowerCase())
    );

    if (filtered.length === 0) {
      this.hideMentionMenu();
      return;
    }

    // 메뉴 항목 생성
    this.mentionMenu.innerHTML = '';
    filtered.forEach(name => {
      const item = document.createElement('div');
      item.className = 'mention-item p-2 hover:bg-indigo-50 cursor-pointer';
      item.dataset.name = name;
      item.innerHTML = `<i class="fas fa-user-circle mr-2 text-gray-500"></i>${name}`;
      item.addEventListener('click', () => this.selectMention(item));
      this.mentionMenu.appendChild(item);
    });

    // 텍스트 영역 위치 기준으로 메뉴 위치 설정
    const rect = textarea.getBoundingClientRect();
    const lineHeight = parseInt(getComputedStyle(textarea).lineHeight);
    this.mentionMenu.style.top = `${rect.top + window.scrollY + lineHeight}px`;
    this.mentionMenu.style.left = `${rect.left + window.scrollX}px`;

    // 메뉴 표시
    this.mentionMenu.classList.remove('hidden');
  }

  /**
   * 선택된 멘션 강조
   */
  highlightSelectedMention() {
    const items = this.mentionMenu.querySelectorAll('.mention-item');
    items.forEach((item, index) => {
      if (index === this.selectedMentionIndex) {
        item.classList.add('bg-indigo-50');
      } else {
        item.classList.remove('bg-indigo-50');
      }
    });
  }

  /**
   * 멘션 선택 처리
   * @param {HTMLElement} item - 선택된 메뉴 항목
   */
  selectMention(item) {
    const name = item.dataset.name;
    const textarea = this.mentionTarget;
    const text = textarea.value;
    const cursorPos = textarea.selectionStart;

    // '@' 문자 위치 찾기
    const lastAtIndex = text.lastIndexOf('@', cursorPos - 1);

    if (lastAtIndex >= 0) {
      // '@쿼리'를 '@이름'으로 대체
      const before = text.substring(0, lastAtIndex);
      const after = text.substring(cursorPos);
      textarea.value = before + '@' + name + ' ' + after;

      // 커서 위치 조정
      const newCursorPos = lastAtIndex + name.length + 2; // '@' + name + ' '
      textarea.setSelectionRange(newCursorPos, newCursorPos);
      textarea.focus();
    }

    // 메뉴 숨김
    this.hideMentionMenu();
  }

  /**
   * 멘션 메뉴 숨김
   */
  hideMentionMenu() {
    if (this.mentionMenu) {
      this.mentionMenu.classList.add('hidden');
    }
    this.mentionQuery = '';
    this.mentionTarget = null;
    this.selectedMentionIndex = -1;
  }
}

// 페이지 로드 시 핸들러 초기화
document.addEventListener('DOMContentLoaded', () => {
  new CommentReplyHandler();
});


