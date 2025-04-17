document.addEventListener('DOMContentLoaded', function () {
  // 모든 투표 버튼에 이벤트 리스너 추가
  const voteButtons = document.querySelectorAll('.vote-button');
  voteButtons.forEach(button => {
    button.addEventListener('click', handleVote);
  });

  // 투표 처리 함수
  function handleVote(event) {
    const button = event.currentTarget;
    const categoryId = button.getAttribute('data-category-id');
    const figureName = button.getAttribute('data-figure-name');
    const sentiment = button.getAttribute('data-sentiment');

    // CSRF 토큰 가져오기 (Spring Security를 사용하는 경우)
    const csrfToken = document.querySelector(
        'meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector(
        'meta[name="_csrf_header"]')?.getAttribute('content');

    // 투표 데이터
    const voteData = {
      sentiment: sentiment
    };

    // 버튼 비활성화 및 로딩 표시
    disableVoteButtons();
    showLoading(button);

    const slug = window.makeSlug(figureName)

    // 1.5초 지연 후 API 호출 (애니메이션이 재생될 시간)
    setTimeout(() => {
      // API 호출
      fetch(`/api/v1/categories/${categoryId}/@${slug}/vote`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(csrfToken && csrfHeader && {[csrfHeader]: csrfToken})
        },
        body: JSON.stringify(voteData)
      })
      .then(response => {
        hideLoading(button);
        if (response.ok || response.status === 204) {
          handleVoteSuccess(sentiment);
          updateReputationSidebar(sentiment);
        } else {
          showError('투표 중 오류가 발생했습니다.');
        }
      })
      .catch(error => {
        hideLoading(button);
        showError(error.message);
        enableVoteButtons();
      });
    }, 1500); // 1.5초 지연
  }

  // 투표 성공 처리
  function handleVoteSuccess(sentiment) {
    const voteContainer = document.getElementById('vote-container');

    // 투표 버튼 컨테이너를 투표 결과로 바꾸기
    let sentimentDisplay = '';
    let sentimentClass = '';
    let icon = '';

    if (sentiment === 'POSITIVE') {
      sentimentDisplay = '숭배';
      sentimentClass = 'sentiment-positive';
      icon = 'fa-crown';
    } else if (sentiment === 'NEUTRAL') {
      sentimentDisplay = '중립';
      sentimentClass = 'sentiment-neutral';
      icon = 'fa-balance-scale';
    } else if (sentiment === 'NEGATIVE') {
      sentimentDisplay = '사형';
      sentimentClass = 'sentiment-negative';
      icon = 'fa-skull';
    }

    const currentDate = new Date();
    const formattedDate = `${currentDate.getFullYear()}-${padZero(
        currentDate.getMonth() + 1)}-${padZero(
        currentDate.getDate())} ${padZero(currentDate.getHours())}:${padZero(
        currentDate.getMinutes())}`;

    const resultHTML = `
        <p class="text-gray-700 mb-2">이 인물에 대해 다음과 같이 평가하셨습니다:</p>
        <div>
            <span class="vote-sentiment-badge ${sentimentClass}">
                <i class="fas ${icon} mr-1"></i> ${sentimentDisplay}
            </span>
        </div>
        <p class="text-sm text-gray-500 mt-3">
            <i class="fas fa-clock mr-1"></i>
            평가일: <span>${formattedDate}</span>
        </p>
        <div class="vote-info mt-4 p-3 bg-blue-50 rounded-lg">
            <p class="text-sm text-blue-700">
                <i class="fas fa-info-circle mr-1"></i>
                투표는 매일 0시에 초기화되어 다시 참여할 수 있습니다. 이전 투표 결과는 전체 통계에 계속 반영됩니다.
            </p>
        </div>
    `;

    voteContainer.innerHTML = resultHTML;
    voteContainer.classList.add('vote-categoryResult');

    // 성공 메시지 표시
    showSuccess();
  }

  // 날짜 포맷팅을 위한 패딩 함수
  function padZero(num) {
    return num.toString().padStart(2, '0');
  }

  // 버튼 비활성화
  function disableVoteButtons() {
    const buttons = document.querySelectorAll('.vote-button');
    buttons.forEach(btn => {
      btn.disabled = true;
      btn.classList.add('opacity-50', 'cursor-not-allowed');
    });
  }

  // 버튼 활성화
  function enableVoteButtons() {
    const buttons = document.querySelectorAll('.vote-button');
    buttons.forEach(btn => {
      btn.disabled = false;
      btn.classList.remove('opacity-50', 'cursor-not-allowed');
    });
  }

  // 로딩 표시
  function showLoading(button) {
    const originalContent = button.innerHTML;
    button.setAttribute('data-original-content', originalContent);
    button.innerHTML = '<i class="fas fa-spinner fa-spin text-3xl"></i><span class="mt-2">처리 중...</span>';
  }

  // 로딩 숨기기
  function hideLoading(button) {
    const originalContent = button.getAttribute('data-original-content');
    if (originalContent) {
      button.innerHTML = originalContent;
    }
  }

  // 성공 메시지 표시
  function showSuccess(figureName, sentiment) {
    window.toastManager.success("투표를 성공적으로 완료했습니다");
  }

  function showError(message) {
    window.toastManager.error(message)
  }

  function updateReputationSidebar(voteType) {
    // 1. 총 투표 수 업데이트
    const totalVotesEl = document.querySelector('[data-total-votes]');
    if (totalVotesEl) {
      const currentTotal = parseInt(totalVotesEl.textContent.trim()) || 0;
      totalVotesEl.textContent = currentTotal + 1;
    }

    // 2. 해당 항목 업데이트 (소문자 변환)
    const typeKey = voteType.toLowerCase();
    const target = document.querySelector(
        `[data-reputation-type="${typeKey}"]`);
    if (target) {
      const rawText = target.textContent.trim(); // 예: "10 (25%)"
      const match = rawText.match(/^(\d+)\s+\((\d+)%\)$/);
      if (match) {
        const currentCount = parseInt(match[1]);
        const total = parseInt(
            document.querySelector('[data-total-votes]').textContent);
        const newCount = currentCount + 1;
        const newPercent = Math.round((newCount / total) * 100);
        target.textContent = `${newCount} (${newPercent}%)`;

        // 3. 진행 바 너비 업데이트
        const bar = target.closest('.transform').querySelector(
            '.reputation-meter > div');
        if (bar) {
          bar.style.width = `${newPercent}%`;
        }
      }
    }
  }
});