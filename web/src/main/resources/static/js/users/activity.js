document.addEventListener('DOMContentLoaded', function () {
  // 활동 필터링 기능
  const filterButtons = document.querySelectorAll('.filter-btn');
  const activityCards = document.querySelectorAll('.activity-card');

  // 현재 표시된 활동 수
  let visibleCount = Math.min(10, activityCards.length);
  updateVisibleCards();

  // 필터 변경 이벤트
  filterButtons.forEach(button => {
    button.addEventListener('click', () => {
      // 현재 활성화된 버튼 비활성화
      document.querySelector('.filter-btn.active').classList.remove('active');

      // 클릭한 버튼 활성화
      button.classList.add('active');

      // 필터링 적용
      const filter = button.dataset.filter;
      filterActivities(filter);
    });
  });

  // 활동 필터링 함수
  function filterActivities(filter) {
    activityCards.forEach(card => {
      // 페이드 아웃 효과 적용
      card.classList.add('fade-out');

      setTimeout(() => {
        if (filter === 'all' || card.dataset.type === filter) {
          card.classList.remove('hidden');
        } else {
          card.classList.add('hidden');
        }

        // 페이드 인 효과 적용
        setTimeout(() => {
          card.classList.remove('fade-out');
        }, 50);
      }, 300);
    });

    // 표시할 카드 수 재설정
    visibleCount = Math.min(10,
        filter === 'all'
            ? activityCards.length
            : Array.from(activityCards).filter(card => card.dataset.type === filter).length
    );

    updateVisibleCards();
    updateLoadMoreButton();
  }

  // 더 불러오기 버튼
  const loadMoreBtn = document.getElementById('loadMoreBtn');

  if (loadMoreBtn) {
    loadMoreBtn.addEventListener('click', () => {
      // 표시할 카드 수 증가
      visibleCount = Math.min(visibleCount + 10, activityCards.length);
      updateVisibleCards();
      updateLoadMoreButton();
    });
  }

  // 표시된 카드 업데이트
  function updateVisibleCards() {
    const activeFilter = document.querySelector('.filter-btn.active').dataset.filter;
    let count = 0;

    activityCards.forEach(card => {
      const matchesFilter = activeFilter === 'all' || card.dataset.type === activeFilter;

      if (matchesFilter) {
        count++;
        if (count <= visibleCount) {
          card.style.display = '';
        } else {
          card.style.display = 'none';
        }
      }
    });
  }

  // 더 불러오기 버튼 상태 업데이트
  function updateLoadMoreButton() {
    const activeFilter = document.querySelector('.filter-btn.active').dataset.filter;
    const matchingCards = Array.from(activityCards).filter(card =>
        activeFilter === 'all' || card.dataset.type === activeFilter
    );

    if (visibleCount >= matchingCards.length) {
      loadMoreBtn.style.display = 'none';
    } else {
      loadMoreBtn.style.display = '';
    }
  }

  // 첫 로드 시 더 불러오기 버튼 상태 설정
  updateLoadMoreButton();

  // 활동 날짜를 상대적 시간으로 표시하는 기능 (예: '3일 전')
  const activityDates = document.querySelectorAll('.activity-date');

  activityDates.forEach(date => {
    // 원래 날짜 텍스트 백업
    const originalDate = date.textContent;

    // 날짜 마우스 오버 시 상대적 시간으로 변경
    date.addEventListener('mouseenter', function() {
      const dateStr = this.textContent;
      const relativeTime = getRelativeTimeFromDateString(dateStr);
      if (relativeTime) {
        this.textContent = relativeTime;
      }
    });

    // 마우스 아웃 시 원래 날짜로 복원
    date.addEventListener('mouseleave', function() {
      this.textContent = originalDate;
    });
  });

  // 상대적 시간 계산 함수 (예: '3일 전')
  function getRelativeTimeFromDateString(dateStr) {
    try {
      const parts = dateStr.match(/(\d{4})년\s+(\d{1,2})월\s+(\d{1,2})일/);
      if (!parts) return null;

      const year = parseInt(parts[1], 10);
      const month = parseInt(parts[2], 10) - 1; // JavaScript의 월은 0부터 시작
      const day = parseInt(parts[3], 10);

      const date = new Date(year, month, day);
      const now = new Date();

      const diffTime = Math.abs(now - date);
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));

      if (diffDays === 0) return '오늘';
      if (diffDays === 1) return '어제';
      if (diffDays < 7) return `${diffDays}일 전`;
      if (diffDays < 30) return `${Math.floor(diffDays / 7)}주 전`;
      if (diffDays < 365) return `${Math.floor(diffDays / 30)}개월 전`;
      return `${Math.floor(diffDays / 365)}년 전`;
    } catch (e) {
      return null;
    }
  }
});