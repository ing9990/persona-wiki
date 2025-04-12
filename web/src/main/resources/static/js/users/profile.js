document.addEventListener('DOMContentLoaded', function() {
  // 상대적 시간 표시 업데이트 (relative-time.js 라이브러리가 있다고 가정)
  updateRelativeTimes();

  // 링크 클릭 시 스무스 스크롤
  setupSmoothScroll();

  // 활동 타임라인 애니메이션
  animateActivityTimeline();
});

/**
 * 상대적 시간(예: "3시간 전") 업데이트
 * 참고: 기존 사이트의 relative-time.js 사용
 */
function updateRelativeTimes() {
  const timeElements = document.querySelectorAll('.comment-date-relative');

  if (timeElements.length > 0 && typeof formatRelativeTime === 'function') {
    timeElements.forEach(element => {
      const originalDate = element.getAttribute('data-original-date');
      if (originalDate) {
        element.textContent = formatRelativeTime(new Date(originalDate));
      }
    });
  }
}

/**
 * 페이지 내 링크 클릭 시 스무스 스크롤 설정
 */
function setupSmoothScroll() {
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
      e.preventDefault();

      const targetId = this.getAttribute('href');
      const targetElement = document.querySelector(targetId);

      if (targetElement) {
        targetElement.scrollIntoView({
          behavior: 'smooth',
          block: 'start'
        });
      }
    });
  });
}

/**
 * 활동 타임라인 애니메이션
 */
function animateActivityTimeline() {
  const activityItems = document.querySelectorAll('.activity-item');

  if (activityItems.length > 0) {
    // IntersectionObserver를 사용하여 요소가 화면에 보일 때 애니메이션 적용
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('animate-fade-in');
          observer.unobserve(entry.target);
        }
      });
    }, { threshold: 0.1 });

    // 각 활동 아이템 관찰 시작
    activityItems.forEach((item, index) => {
      // 아이템마다 약간의 지연 시간 추가
      setTimeout(() => {
        observer.observe(item);
      }, index * 100); // 각 항목마다 100ms 간격으로 지연
    });
  }
}