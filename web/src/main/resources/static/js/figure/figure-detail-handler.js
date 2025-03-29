/**
 * 인물 상세 페이지 핸들러
 * 투표, 댓글, 공유 기능 및 페이징을 담당합니다.
 */
class FigureDetailHandler {
  constructor() {
    this.initializeElements();
    this.addEventListeners();
    this.checkPreviousVote();
    this.checkFlashMessages();
  }

  /**
   * DOM 요소 초기화
   */
  initializeElements() {
    this.voteButtons = document.querySelectorAll('.vote-button');
    this.commentLikeButtons = document.querySelectorAll(
        '[data-action="like-comment"]');
    this.commentDislikeButtons = document.querySelectorAll(
        '[data-action="dislike-comment"]');
    this.shareButtons = document.querySelectorAll('[data-action^="share-"]');
    this.paginationLinks = document.querySelectorAll('.pagination a');
    this.figureId = this.getFigureId();
    this.currentPage = this.getCurrentPage();
    this.commentForm = document.querySelector('form[action*="/comment"]');
  }

  /**
   * 현재 페이지 번호 가져오기
   * @returns {number} 페이지 번호 (0부터 시작)
   */
  getCurrentPage() {
    // URL에서 page 파라미터 값을 추출
    const urlParams = new URLSearchParams(window.location.search);
    const page = urlParams.get('page');
    return page ? parseInt(page) : 0;
  }

  /**
   * 플래시 메시지 확인 및 처리
   */
  checkFlashMessages() {
    // 세션에서 성공/에러 메시지 확인
    const successMessage = sessionStorage.getItem('success');
    const errorMessage = sessionStorage.getItem('error');

    if (successMessage) {
      window.ToastManager.show(successMessage);
      sessionStorage.removeItem('success');
    }

    if (errorMessage) {
      window.ToastManager.show(errorMessage, true);
      sessionStorage.removeItem('error');
    }
  }

  /**
   * 이벤트 리스너 추가
   */
  addEventListeners() {
    // 투표 버튼 이벤트
    this.voteButtons.forEach(button => {
      button.addEventListener('click', this.handleVote.bind(this));
    });

    // 댓글 좋아요/싫어요 버튼 이벤트
    this.commentLikeButtons.forEach(button => {
      button.addEventListener('click',
          this.handleCommentInteraction.bind(this, true));
    });
    this.commentDislikeButtons.forEach(button => {
      button.addEventListener('click',
          this.handleCommentInteraction.bind(this, false));
    });

    // 공유 버튼 이벤트
    this.shareButtons.forEach(button => {
      button.addEventListener('click', this.handleShare.bind(this));
    });

    // 페이지네이션 이벤트 (페이지 이동 시 스크롤 위치 기억)
    this.paginationLinks.forEach(link => {
      link.addEventListener('click', this.handlePaginationClick.bind(this));
    });

    // 댓글 폼 제출 이벤트
    if (this.commentForm) {
      this.commentForm.addEventListener('submit',
          this.handleCommentSubmit.bind(this));
    }

    // 페이지 로드 시 댓글 섹션으로 스크롤 (페이지 이동 후)
    if (this.currentPage > 0) {
      this.scrollToCommentSection();
    }
  }

  /**
   * 댓글 폼 제출 처리
   * @param {Event} event - 폼 제출 이벤트
   */
  handleCommentSubmit(event) {
    const textarea = this.commentForm.querySelector('textarea');
    const content = textarea.value.trim();

    // 내용이 비어있는지 확인
    if (!content) {
      event.preventDefault();
      window.ToastManager.show('댓글 내용을 입력해주세요.', true);
      textarea.focus();
      return false;
    }

    // 폼 제출 시 세션에 상태 저장 (페이지 이동 후에도 댓글 위치로 스크롤하기 위함)
    sessionStorage.setItem('commentSubmitted', 'true');

    return true;
  }

  /**
   * 페이지네이션 클릭 처리
   * @param {Event} event - 클릭 이벤트
   */
  handlePaginationClick(event) {
    // 페이지 이동 전 현재 스크롤 위치를 세션 스토리지에 저장
    sessionStorage.setItem('commentScrollPosition', 'true');
  }

  /**
   * 댓글 섹션으로 스크롤
   */
  scrollToCommentSection() {
    // 스크롤 위치 복원 여부 확인
    const shouldScroll = sessionStorage.getItem('commentScrollPosition') ||
        sessionStorage.getItem('commentSubmitted');

    if (shouldScroll) {
      // 댓글 섹션 찾기
      const commentsSection = document.querySelector('h2 .fa-comments').closest(
          'section');

      if (commentsSection) {
        // 댓글 섹션으로 스크롤
        setTimeout(() => {
          commentsSection.scrollIntoView({behavior: 'smooth'});
          // 세션 스토리지 초기화
          sessionStorage.removeItem('commentScrollPosition');
          sessionStorage.removeItem('commentSubmitted');
        }, 100);
      }
    }
  }

  /**
   * 인물 ID 추출
   * @returns {string} 인물 ID
   */
  getFigureId() {
    // 1. data 속성으로 ID 직접 전달
    const figureSection = document.querySelector('.profile-header');
    if (figureSection) {
      const figureId = figureSection.getAttribute('data-figure-id');
      if (figureId) {
        return figureId;
      }
    }

    // 2. URL에서 ID 추출 (예: /{categoryId}/@{figureName} 형식)
    const path = window.location.pathname;
    const pathParts = path.split('/');

    console.log('Path parts:', pathParts);

    return null;
  }

  /**
   * 이전 투표 여부 확인
   */
  checkPreviousVote() {
    const votedSentiment = localStorage.getItem(`voted_${this.figureId}`);

    if (votedSentiment) {
      this.disableVoteButtons();
      this.showVotedMessage(votedSentiment);
    }
  }

  /**
   * 투표 버튼 비활성화
   */
  disableVoteButtons() {
    this.voteButtons.forEach(button => {
      button.disabled = true;
      button.classList.add('opacity-50', 'cursor-not-allowed');
      button.classList.remove(
          'hover:border-green-500',
          'hover:border-blue-500',
          'hover:border-red-500',
          'hover:bg-green-50',
          'hover:bg-blue-50',
          'hover:bg-red-50'
      );
    });
  }

  /**
   * 투표 완료 메시지 표시
   * @param {string} sentiment - 투표 감정
   */
  showVotedMessage(sentiment) {
    const voteSection = this.voteButtons[0].closest('section');
    const messageDiv = document.createElement('div');

    let sentimentText = '중립적';
    let sentimentClass = 'text-blue-600';
    let sentimentIcon = 'fa-balance-scale';

    if (sentiment === 'POSITIVE') {
      sentimentText = '숭배';
      sentimentClass = 'text-green-600';
      sentimentIcon = 'fa-crown';
    } else if (sentiment === 'NEGATIVE') {
      sentimentText = '사형';
      sentimentClass = 'text-red-600';
      sentimentIcon = 'fa-skull';
    }

    messageDiv.className = 'mt-4 text-center py-2 border-t border-gray-200';
    messageDiv.innerHTML = `
            <p class="text-gray-600">이미 이 인물에 대해 <span class="${sentimentClass} font-bold"><i class="fas ${sentimentIcon} mr-1"></i>${sentimentText}</span> 평가를 하셨습니다.</p>
        `;

    voteSection.appendChild(messageDiv);
  }

  /**
   * 투표 처리
   * @param {Event} event - 클릭 이벤트
   */
  handleVote(event) {
    const sentiment = event.currentTarget.dataset.sentiment;

    fetch(`/api/figures/${this.figureId}/vote`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({sentiment})
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('투표 등록에 실패했습니다.');
      }
      return response.json();
    })
    .then(() => {
      // 투표 내역 localStorage에 저장
      localStorage.setItem(`voted_${this.figureId}`, sentiment);

      // 투표 버튼 비활성화
      this.disableVoteButtons();

      // 투표 완료 메시지 표시
      this.showVotedMessage(sentiment);

      // 성공 메시지 표시
      let message = '중립 평가를 등록했습니다.';
      if (sentiment === 'POSITIVE') {
        message = '숭배 평가를 등록했습니다.';
      } else if (sentiment === 'NEGATIVE') {
        message = '사형 평가를 등록했습니다.';
      }
      window.ToastManager.show(message);

      // 페이지 새로고침 (통계 업데이트)
      setTimeout(() => {
        location.reload();
      }, 1000);
    })
    .catch(error => {
      console.error('투표 등록 실패:', error);
      window.ToastManager.show(error.message, true);
    });
  }

  /**
   * 댓글 좋아요/싫어요 처리
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
   * 공유 기능 처리
   * @param {Event} event - 클릭 이벤트
   */
  handleShare(event) {
    const action = event.currentTarget.dataset.action;
    const url = encodeURIComponent(window.location.href);
    const title = encodeURIComponent(document.title);

    switch (action) {
      case 'share-twitter':
        window.open(`https://twitter.com/intent/tweet?text=${title}&url=${url}`,
            '_blank');
        break;
      case 'share-facebook':
        window.open(`https://www.facebook.com/sharer/sharer.php?u=${url}`,
            '_blank');
        break;
      case 'share-link':
        navigator.clipboard.writeText(window.location.href)
        .then(() => window.ToastManager.show('링크가 복사되었습니다.'))
        .catch(err => {
          console.error('클립보드 복사 실패:', err);
          window.ToastManager.show('링크 복사에 실패했습니다.', true);
        });
        break;
    }
  }
}

// 페이지 로드 시 핸들러 초기화
document.addEventListener('DOMContentLoaded', () => {
  new FigureDetailHandler();
});