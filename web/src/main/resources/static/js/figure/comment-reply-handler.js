/**
 * 댓글 및 답글 관련 기능을 처리하는 핸들러
 */
class CommentReplyHandler {
  constructor() {
    this.initializeElements();
    this.bindEvents();

    // 페이지 로드 후 약간의 지연을 두고 실험 코드 실행
    setTimeout(() => {
      this.debugElements();
      this.addDirectClickHandler();
    }, 500);
  }

  /**
   * DOM 요소 초기화
   */
  initializeElements() {
    // 답글 템플릿
    this.replyTemplate = document.getElementById('reply-template');
  }

  /**
   * 중요 요소들의 상태를 확인합니다 (디버깅용)
   */
  debugElements() {
    console.log('댓글 요소 디버깅:');

    // 답글 토글 버튼 확인
    const replyToggleButtons = document.querySelectorAll('.reply-toggle-btn');
    console.log(`답글 달기 버튼: ${replyToggleButtons.length}개 발견`);
    replyToggleButtons.forEach((btn, i) => {
      console.log(`${i + 1}번째 답글 달기 버튼:`, {
        commentId: btn.getAttribute('data-comment-id'),
        html: btn.outerHTML
      });
    });

    // 답글 폼 확인
    const replyForms = document.querySelectorAll('[id^="reply-form-"]');
    console.log(`답글 폼: ${replyForms.length}개 발견`);
    replyForms.forEach((form, i) => {
      console.log(`${i + 1}번째 답글 폼:`, {
        id: form.id,
        display: window.getComputedStyle(form).display,
        visibility: window.getComputedStyle(form).visibility,
        overflow: window.getComputedStyle(form).overflow,
        height: window.getComputedStyle(form).height,
        opacity: window.getComputedStyle(form).opacity
      });
    });
  }

  /**
   * 특정 댓글의 답글 버튼에 직접 이벤트 리스너 추가 (실험용)
   */
  addDirectClickHandler() {
    document.querySelectorAll('.reply-toggle-btn').forEach(button => {
      // 기존 클릭 핸들러 제거 (이벤트 중복 방지)
      const clonedButton = button.cloneNode(true);
      button.parentNode.replaceChild(clonedButton, button);

      // 새 클릭 핸들러 추가
      clonedButton.addEventListener('click', (e) => {
        console.log('답글 버튼 직접 클릭됨!');
        const commentId = clonedButton.getAttribute('data-comment-id');
        if (!commentId) {
          console.error('댓글 ID를 찾을 수 없습니다.');
          return;
        }

        const replyForm = document.getElementById(`reply-form-${commentId}`);
        if (!replyForm) {
          console.error(`답글 폼을 찾을 수 없습니다: reply-form-${commentId}`);
          return;
        }

        console.log('답글 폼 표시 상태 변경 전:', replyForm.style.display);

        // 모든 답글 폼 숨기기
        document.querySelectorAll('[id^="reply-form-"]').forEach(form => {
          if (form.id !== `reply-form-${commentId}`) {
            form.style.display = 'none';
          }
        });

        // 현재 폼 토글
        replyForm.style.display = replyForm.style.display === 'none'
        || !replyForm.style.display ? 'block' : 'none';

        console.log('답글 폼 표시 상태 변경 후:', replyForm.style.display);

        // 폼이 표시되면 포커스
        if (replyForm.style.display === 'block') {
          const textarea = replyForm.querySelector('textarea');
          if (textarea) {
            textarea.focus();
          }
        }

        e.preventDefault();
        e.stopPropagation();
      });
    });

    // 답글 취소 버튼에도 직접 이벤트 리스너 추가
    document.querySelectorAll('.cancel-reply-btn').forEach(button => {
      const clonedButton = button.cloneNode(true);
      button.parentNode.replaceChild(clonedButton, button);

      clonedButton.addEventListener('click', (e) => {
        const commentId = clonedButton.getAttribute('data-comment-id');
        if (!commentId) {
          return;
        }

        const replyForm = document.getElementById(`reply-form-${commentId}`);
        if (replyForm) {
          replyForm.style.display = 'none';
        }

        e.preventDefault();
        e.stopPropagation();
      });
    });
  }

  /**
   * 이벤트 바인딩
   */
  bindEvents() {
    // 댓글 좋아요/싫어요 버튼
    document.querySelectorAll('[data-action="like-comment"]').forEach(
        button => {
          button.addEventListener('click', (e) => {
            this.handleCommentInteraction(true, button);
            e.preventDefault();
          });
        });

    document.querySelectorAll('[data-action="dislike-comment"]').forEach(
        button => {
          button.addEventListener('click', (e) => {
            this.handleCommentInteraction(false, button);
            e.preventDefault();
          });
        });

    // 답글 보기 버튼
    document.querySelectorAll('.view-replies-btn').forEach(button => {
      button.addEventListener('click', (e) => {
        this.toggleReplies(button);
        e.preventDefault();
      });
    });
  }

  /**
   * 답글 목록을 토글(보기/숨기기)합니다
   * @param {HTMLElement} button - 답글 보기 버튼
   */
  toggleReplies(button) {
    const commentId = button.getAttribute('data-comment-id');
    if (!commentId) {
      return;
    }

    const repliesContainer = document.getElementById(`replies-${commentId}`);
    if (!repliesContainer) {
      return;
    }

    // 컨테이너가 숨겨져 있으면 답글 로드, 아니면 숨김
    if (repliesContainer.style.display === 'none'
        || !repliesContainer.style.display) {
      // 컨테이너 표시
      repliesContainer.style.display = 'block';

      // 버튼 텍스트 변경
      const replyCount = button.getAttribute('data-reply-count');
      button.innerHTML = `<span>답글 숨기기</span><i class="fas fa-chevron-up ml-1 text-xs"></i>`;

      // 아직 로드되지 않았으면 답글 로드
      if (!repliesContainer.getAttribute('data-loaded')) {
        this.loadReplies(commentId, repliesContainer);
      }
    } else {
      // 컨테이너 숨김
      repliesContainer.style.display = 'none';

      // 버튼 텍스트 변경
      const replyCount = button.getAttribute('data-reply-count');
      button.innerHTML = `<span>답글 ${replyCount}개</span><i class="fas fa-chevron-down ml-1 text-xs"></i>`;
    }
  }

  /**
   * 답글 목록을 로드합니다
   * @param {string} commentId - 댓글 ID
   * @param {HTMLElement} container - 답글을 표시할 컨테이너
   */
  loadReplies(commentId, container) {
    // 로딩 표시
    const loadingElement = container.querySelector('.replies-loading');
    if (loadingElement) {
      loadingElement.style.display = 'block';
    }

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
      if (loadingElement) {
        loadingElement.style.display = 'none';
      }

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

      // 답글 보기 버튼 아이콘 업데이트
      const viewButton = document.querySelector(
          `.view-replies-btn[data-comment-id="${commentId}"]`);
      if (viewButton) {
        viewButton.innerHTML = `<span>답글 숨기기</span><i class="fas fa-chevron-up ml-1 text-xs"></i>`;
      }
    })
    .catch(error => {
      console.error('Error:', error);
      if (loadingElement) {
        loadingElement.style.display = 'none';
      }

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
    // 간소화된 답글 요소 생성 (소셜미디어 스타일)
    const replyElement = document.createElement('div');
    replyElement.className = 'reply-item';
    replyElement.dataset.replyId = reply.id;

    // 작성자 정보와 내용
    replyElement.innerHTML = `
      <div class="flex justify-between mb-1">
        <div class="text-sm font-medium">
          <i class="fas fa-user-circle text-gray-500 mr-1"></i>
          <span>익명</span>
        </div>
        <span class="text-xs text-gray-500">${this.formatDate(reply.createdAt)}</span>
      </div>
      <p class="text-gray-800 mb-2">${reply.content}</p>
      <div class="flex items-center text-xs">
        <button class="flex items-center mr-3 text-gray-500 hover:text-green-500 transition reply-like-btn" data-reply-id="${reply.id}">
          <i class="fas fa-thumbs-up mr-1"></i>
          <span class="reply-likes">${reply.likes}</span>
        </button>
        <button class="flex items-center mr-3 text-gray-500 hover:text-red-500 transition reply-dislike-btn" data-reply-id="${reply.id}">
          <i class="fas fa-thumbs-down mr-1"></i>
          <span class="reply-dislikes">${reply.dislikes}</span>
        </button>
        <button class="text-gray-500 hover:text-indigo-500 transition reply-to-reply-btn">
          <i class="fas fa-reply mr-1"></i>
          <span>답글 달기</span>
        </button>
      </div>
    `;

    // 좋아요/싫어요 버튼 이벤트
    const likeButton = replyElement.querySelector('.reply-like-btn');
    const dislikeButton = replyElement.querySelector('.reply-dislike-btn');

    if (likeButton) {
      likeButton.addEventListener('click',
          () => this.handleReplyInteraction(reply.id, true));
    }

    if (dislikeButton) {
      dislikeButton.addEventListener('click',
          () => this.handleReplyInteraction(reply.id, false));
    }

    // 답글 달기 버튼 이벤트
    const replyButton = replyElement.querySelector('.reply-to-reply-btn');
    if (replyButton) {
      replyButton.addEventListener('click', () => {
        // 루트 댓글의 답글 폼 사용
        const rootId = this.findRootCommentId(reply.id) || reply.rootId;
        if (rootId) {
          const rootReplyForm = document.getElementById(`reply-form-${rootId}`);
          if (rootReplyForm) {
            // 답글 폼 표시 및 포커스
            rootReplyForm.style.display = 'block';
            const textarea = rootReplyForm.querySelector('textarea');
            if (textarea) {
              textarea.value = `@익명 `;
              textarea.focus();
            }
          }
        }
      });
    }

    return replyElement;
  }

  /**
   * 루트 댓글 ID를 찾습니다
   * @param {string} replyId - 답글 ID
   * @returns {string|null} - 루트 댓글 ID 또는 null
   */
  findRootCommentId(replyId) {
    // DOM 구조를 통해 루트 댓글 ID를 찾는 방법
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
    if (!dateString) {
      return '';
    }

    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now - date;
    const diffMin = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    // 소셜미디어 스타일의 날짜 표시 (예: "5분 전", "3시간 전", "어제", "3일 전")
    if (diffMin < 1) {
      return '방금 전';
    } else if (diffMin < 60) {
      return `${diffMin}분 전`;
    } else if (diffHours < 24) {
      return `${diffHours}시간 전`;
    } else if (diffDays === 1) {
      return '어제';
    } else if (diffDays < 7) {
      return `${diffDays}일 전`;
    } else {
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      });
    }
  }

  /**
   * 댓글 좋아요/싫어요 처리
   * @param {boolean} isLike - 좋아요(true) 또는 싫어요(false)
   * @param {HTMLElement} button - 버튼 요소
   */
  handleCommentInteraction(isLike, button) {
    const commentId = button.getAttribute('data-comment-id');
    if (!commentId) {
      return;
    }

    const actionEndpoint = isLike ? 'like' : 'dislike';
    const countElement = button.querySelector('.like-count, .dislike-count');
    if (!countElement) {
      return;
    }

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
        if (countElement) {
          countElement.textContent = isLike ? data.likes : data.dislikes;
        }
      }

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

// 페이지 로드 시 핸들러 초기화
document.addEventListener('DOMContentLoaded', () => {
  new CommentReplyHandler();
});