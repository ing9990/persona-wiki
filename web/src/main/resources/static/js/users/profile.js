document.addEventListener('DOMContentLoaded', function () {
  // 레벨별 별명
  const levelTitles = [
    "병아리",     // 0레벨
    "견습생",     // 1레벨
    "관찰자",     // 2레벨
    "평론가",     // 3레벨
    "분석가",     // 4레벨
    "영향력자",   // 5레벨
    "전문가",     // 6레벨
    "현자",       // 7레벨
    "대가",       // 8레벨
    "거장",       // 9레벨
    "인물의 신"   // 10레벨
  ];

  // 피보나치 수열 기반 레벨 요구 점수 계산
  function calculateLevelRequirement(level) {
    if (level <= 0) {
      return 0;
    }
    if (level === 1) {
      return 100;
    }
    if (level === 2) {
      return 200;
    }
    if (level === 3) {
      return 300;
    }
    if (level === 4) {
      return 500;
    }

    // 5레벨 이상은 피보나치로 계산
    let a = 300; // 3레벨 요구치
    let b = 500; // 4레벨 요구치
    let result = 0;

    for (let i = 5; i <= level; i++) {
      result = a + b;
      a = b;
      b = result;
    }

    return result;
  }

  // 사용자 레벨 및 진행도 계산
  function calculateUserLevel(prestige) {
    let level = 0;
    let accumulatedPoints = 0;
    let nextLevelRequirement = calculateLevelRequirement(level + 1);

    while (prestige >= accumulatedPoints + nextLevelRequirement) {
      accumulatedPoints += nextLevelRequirement;
      level++;
      nextLevelRequirement = calculateLevelRequirement(level + 1);
    }

    // 다음 레벨까지 남은 포인트
    const pointsForNextLevel = nextLevelRequirement;
    const currentLevelPoints = prestige - accumulatedPoints;
    const progressPercentage = Math.floor(
        (currentLevelPoints / pointsForNextLevel) * 100);

    return {
      level,
      currentLevelPoints,
      pointsForNextLevel,
      progressPercentage,
      totalPrestige: prestige,
      title: getLevelTitle(level)
    };
  }

  // 레벨에 따른 별명 반환
  function getLevelTitle(level) {
    if (level < 0) {
      return levelTitles[0];
    }
    if (level >= levelTitles.length) {
      return levelTitles[levelTitles.length - 1];
    }
    return levelTitles[level];
  }

  // 명성 레벨 표시 업데이트
  function updatePrestigeDisplay() {
    const prestigeElement = document.getElementById('userPrestige');
    if (!prestigeElement) {
      return;
    }

    const prestige = parseInt(
        prestigeElement.getAttribute('data-prestige') || '0');
    const levelInfo = calculateUserLevel(prestige);

    // 프로그레스 바 업데이트
    const progressBar = document.querySelector('.prestige-progress-bar');
    if (progressBar) {
      progressBar.style.width = `${levelInfo.progressPercentage}%`;
    }

    // 레벨 배지 업데이트
    const prestigeBadge = document.querySelector('.prestige-badge');
    if (prestigeBadge) {
      // 기존 레벨 클래스 제거
      for (let i = 0; i <= 10; i++) {
        prestigeBadge.classList.remove(`level-${i}`);
      }

      // 현재 레벨 클래스 추가
      prestigeBadge.classList.add(`level-${levelInfo.level}`);
    }

    // 레벨 카드 업데이트
    const levelCard = document.querySelector('.prestige-level-card');
    if (levelCard) {
      // 기존 레벨 클래스 제거
      for (let i = 0; i <= 10; i++) {
        levelCard.classList.remove(`level-${i}`);
      }

      // 현재 레벨 클래스 추가
      levelCard.classList.add(`level-${levelInfo.level}`);
    }

    // 레벨 별명 업데이트
    const titleElements = document.querySelectorAll('.title-text');
    if (titleElements.length > 0) {
      titleElements.forEach(element => {
        element.textContent = levelInfo.title;
      });
    }
  }

  // 상대적 시간 표시 업데이트
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

  // 페이지 내 링크 클릭 시 스무스 스크롤 설정
  function setupSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
      anchor.addEventListener('click', function (e) {
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

  // 활동 타임라인 애니메이션
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
      }, {threshold: 0.1});

      // 각 활동 아이템 관찰 시작
      activityItems.forEach((item, index) => {
        // 아이템마다 약간의 지연 시간 추가
        setTimeout(() => {
          observer.observe(item);
        }, index * 100); // 각 항목마다 100ms 간격으로 지연
      });
    }
  }

  // 통계 카드 애니메이션
  function animateStatCards() {
    const statCards = document.querySelectorAll('.stat-card');

    statCards.forEach((card, index) => {
      setTimeout(() => {
        card.classList.add('animate-fade-in');
      }, index * 100);
    });
  }

  // 레벨 카드 툴팁 설정
  function setupLevelTooltips() {
    const levelElements = document.querySelectorAll(
        '.prestige-badge, .prestige-level-card, .prestige-level-number');

    levelElements.forEach(element => {
      element.setAttribute('title', '레벨이 올라갈수록 다양한 혜택이 주어집니다!');
    });
  }

  // 기본 함수 실행
  updateRelativeTimes();
  setupSmoothScroll();
  animateActivityTimeline();
  animateStatCards();

  // 명성 레벨 시스템 초기화
  updatePrestigeDisplay();
  setupLevelTooltips();
});