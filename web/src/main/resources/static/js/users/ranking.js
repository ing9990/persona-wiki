/**
 * 사용자 랭킹 페이지 자바스크립트
 */
document.addEventListener('DOMContentLoaded', function () {
  // TOP 3 카드 애니메이션
  initTopRanksAnimation();

  // 현재 사용자 행으로 스크롤
  scrollToCurrentUser();

  // 프로필 이미지 효과
  initProfileEffects();

  // 반응형 테이블 최적화
  optimizeTableForMobile();

  // 스크롤 애니메이션
  initScrollAnimations();

  // 페이지네이션 관련 이벤트
  initPaginationEvents();
});

/**
 * TOP 3 카드 애니메이션 초기화
 */
function initTopRanksAnimation() {
  // 카드 선택
  const cards = document.querySelectorAll('.rank-card');

  // 초기 상태 설정 (모두 숨김)
  cards.forEach(card => {
    card.style.opacity = '0';
    card.style.transform = card.classList.contains('gold')
        ? 'translateY(30px) scale(1.1)'
        : 'translateY(30px) scale(0.95)';
  });

  // 순차적으로 표시
  setTimeout(() => {
    // 1등 카드 표시 (가운데)
    const goldCard = document.querySelector('.rank-card.gold');
    if (goldCard) {
      goldCard.style.transition = 'all 0.8s cubic-bezier(0.175, 0.885, 0.32, 1.275)';
      goldCard.style.opacity = '1';
      goldCard.style.transform = 'translateY(0) scale(1.1)';

      // 왕관 효과
      const crown = goldCard.querySelector('.crown-badge');
      if (crown) {
        setTimeout(() => {
          crown.style.transition = 'transform 0.5s cubic-bezier(0.18, 0.89, 0.32, 1.28)';
          crown.style.transform = 'translateX(-50%) scale(1.2)';

          setTimeout(() => {
            crown.style.transform = 'translateX(-50%) scale(1)';
          }, 300);
        }, 500);
      }

      // 프로필 효과
      const profileContainer = goldCard.querySelector(
          '.gold-profile-container');
      if (profileContainer) {
        setTimeout(() => {
          profileContainer.classList.add('pulse-effect');
        }, 800);
      }

      // 점수 효과
      const scoreValue = goldCard.querySelector('.gold-score');
      if (scoreValue) {
        setTimeout(() => {
          scoreValue.style.transition = 'all 0.5s ease';
          scoreValue.style.textShadow = '0 0 5px rgba(255, 215, 0, 0.7)';
          scoreValue.style.color = '#B8860B';
        }, 1000);
      }
    }

    // 2등 카드 표시 (왼쪽)
    setTimeout(() => {
      const silverCard = document.querySelector('.rank-card.silver');
      if (silverCard) {
        silverCard.style.transition = 'all 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275)';
        silverCard.style.opacity = '1';
        silverCard.style.transform = 'translateY(0) scale(0.95)';

        // 배지 효과
        const silverBadge = silverCard.querySelector('.silver-badge');
        if (silverBadge) {
          setTimeout(() => {
            silverBadge.style.transition = 'transform 0.4s ease';
            silverBadge.style.transform = 'translateX(-50%) scale(1.1)';
            setTimeout(() => {
              silverBadge.style.transform = 'translateX(-50%) scale(1)';
            }, 300);
          }, 300);
        }
      }
    }, 400);

    // 3등 카드 표시 (오른쪽)
    setTimeout(() => {
      const bronzeCard = document.querySelector('.rank-card.bronze');
      if (bronzeCard) {
        bronzeCard.style.transition = 'all 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275)';
        bronzeCard.style.opacity = '1';
        bronzeCard.style.transform = 'translateY(0) scale(0.95)';

        // 배지 효과
        const bronzeBadge = bronzeCard.querySelector('.bronze-badge');
        if (bronzeBadge) {
          setTimeout(() => {
            bronzeBadge.style.transition = 'transform 0.4s ease';
            bronzeBadge.style.transform = 'translateX(-50%) scale(1.1)';
            setTimeout(() => {
              bronzeBadge.style.transform = 'translateX(-50%) scale(1)';
            }, 300);
          }, 300);
        }
      }
    }, 800);
  }, 300);
}

/**
 * 현재 사용자 행으로 스크롤
 */
function scrollToCurrentUser() {
  const currentUserRow = document.querySelector('tr.current-user');

  if (currentUserRow) {
    // TOP 3 애니메이션 후에 스크롤 (2초 후)
    setTimeout(() => {
      const offset = currentUserRow.offsetTop - window.innerHeight / 2;

      // 부드러운 스크롤 적용
      window.scrollTo({
        top: offset,
        behavior: 'smooth'
      });

      // 행 강조 효과
      currentUserRow.style.transition = 'background-color 0.7s ease';
      currentUserRow.style.backgroundColor = 'rgba(99, 102, 241, 0.2)';

      setTimeout(() => {
        currentUserRow.style.backgroundColor = '';
      }, 2000);
    }, 2000);
  }
}

/**
 * 프로필 이미지 효과 초기화
 */
function initProfileEffects() {
  // 테이블 프로필 이미지 호버 효과
  const tableImages = document.querySelectorAll('.table-profile-image');
  tableImages.forEach(img => {
    img.addEventListener('mouseenter', function () {
      this.style.transform = 'scale(1.1)';
    });

    img.addEventListener('mouseleave', function () {
      this.style.transform = 'scale(1)';
    });

    // 이미지 로드 오류 시 기본 이미지 설정
    img.addEventListener('error', function () {
      this.src = '/img/profile-placeholder.svg';
    });
  });

  // TOP 3 프로필 이미지 로드 효과
  const topImages = document.querySelectorAll('.profile-image-container img');
  topImages.forEach(img => {
    img.style.opacity = '0';
    img.style.transition = 'opacity 0.5s ease';

    img.onload = function () {
      this.style.opacity = '1';
    };

    // 이미지가 이미 캐시되어 있는 경우 처리
    if (img.complete) {
      img.style.opacity = '1';
    }

    // 이미지 로드 오류 시 기본 이미지 설정
    img.addEventListener('error', function () {
      this.src = '/img/profile-placeholder.svg';
      this.style.opacity = '1';
    });
  });
}

/**
 * 반응형 테이블 최적화
 */
function optimizeTableForMobile() {
  function adjustColumns() {
    const isMobile = window.innerWidth < 768;
    const levelColumn = document.querySelectorAll(
        'th:nth-child(3), td:nth-child(3)');
    const bioColumn = document.querySelectorAll(
        'th:nth-child(5), td:nth-child(5)');

    levelColumn.forEach(col => {
      col.style.display = isMobile ? 'none' : '';
    });

    bioColumn.forEach(col => {
      col.style.display = isMobile ? 'none' : '';
    });

    // 모바일에서 사용자 칼럼 너비 조정
    const userColumns = document.querySelectorAll(
        'th:nth-child(2), td:nth-child(2)');
    userColumns.forEach(col => {
      if (isMobile) {
        col.style.width = '60%';
      } else {
        col.style.width = '';
      }
    });

    // 모바일에서 점수 칼럼 너비 조정
    const scoreColumns = document.querySelectorAll(
        'th:nth-child(4), td:nth-child(4)');
    scoreColumns.forEach(col => {
      if (isMobile) {
        col.style.width = '30%';
      } else {
        col.style.width = '';
      }
    });

    // 모바일에서 TOP 3 카드 조정
    if (isMobile) {
      const goldCard = document.querySelector('.rank-card.gold');
      if (goldCard && goldCard.style.transform) {
        goldCard.style.transform = 'translateY(0) scale(1)';
      }

      const silverCard = document.querySelector('.rank-card.silver');
      if (silverCard && silverCard.style.transform) {
        silverCard.style.transform = 'translateY(0) scale(1)';
      }

      const bronzeCard = document.querySelector('.rank-card.bronze');
      if (bronzeCard && bronzeCard.style.transform) {
        bronzeCard.style.transform = 'translateY(0) scale(1)';
      }
    } else {
      // 데스크톱에서 원래 크기로 복원
      const goldCard = document.querySelector('.rank-card.gold');
      if (goldCard && goldCard.style.opacity === '1') {
        goldCard.style.transform = 'translateY(0) scale(1.1)';
      }

      const silverCard = document.querySelector('.rank-card.silver');
      if (silverCard && silverCard.style.opacity === '1') {
        silverCard.style.transform = 'translateY(0) scale(0.95)';
      }

      const bronzeCard = document.querySelector('.rank-card.bronze');
      if (bronzeCard && bronzeCard.style.opacity === '1') {
        bronzeCard.style.transform = 'translateY(0) scale(0.95)';
      }
    }
  }

  // 초기 실행
  adjustColumns();

  // 화면 크기 변경 시 실행
  window.addEventListener('resize', adjustColumns);
}

/**
 * 스크롤 애니메이션 초기화
 */
function initScrollAnimations() {
  // 애니메이션 요소 선택
  const animatedElements = document.querySelectorAll('.animate-on-scroll');

  // 초기 로드 시 화면에 보이는 요소들 애니메이션
  triggerInitialAnimations();

  // 스크롤 이벤트 리스너
  window.addEventListener('scroll', function () {
    triggerScrollAnimations();
  });

  // 초기 로드 시 화면에 보이는 요소들 애니메이션 함수
  function triggerInitialAnimations() {
    animatedElements.forEach(element => {
      if (isElementInViewport(element)) {
        element.classList.add('visible');
      }
    });
  }

  // 스크롤 시 요소들 애니메이션 함수
  function triggerScrollAnimations() {
    animatedElements.forEach(element => {
      if (isElementInViewport(element) && !element.classList.contains(
          'visible')) {
        element.classList.add('visible');
      }
    });
  }

  // 요소가 뷰포트에 있는지 확인하는 함수
  function isElementInViewport(element) {
    const rect = element.getBoundingClientRect();
    return (
        rect.top <= (window.innerHeight
            || document.documentElement.clientHeight) * 0.9 &&
        rect.bottom >= 0 &&
        rect.left >= 0 &&
        rect.right <= (window.innerWidth
            || document.documentElement.clientWidth)
    );
  }

  // 테이블 행에 스크롤 애니메이션 적용
  const tableRows = document.querySelectorAll('.ranking-table tbody tr');
  tableRows.forEach((row, index) => {
    row.classList.add('animate-on-scroll');
    row.style.transitionDelay = `${0.1 + (index * 0.05)}s`;
  });

  // 초기 애니메이션 트리거
  triggerScrollAnimations();
}

/**
 * 페이지네이션 관련 이벤트 초기화
 */
function initPaginationEvents() {
  const paginationLinks = document.querySelectorAll('.pagination-link');

  paginationLinks.forEach(link => {
    // 호버 효과
    link.addEventListener('mouseenter', function () {
      if (!this.classList.contains('disabled')) {
        this.style.transform = 'translateY(-2px)';
      }
    });

    link.addEventListener('mouseleave', function () {
      this.style.transform = '';
    });

    // 클릭 효과
    link.addEventListener('click', function (e) {
      if (this.classList.contains('disabled')) {
        e.preventDefault();
        return false;
      }

      this.style.transform = 'scale(0.95)';
      setTimeout(() => {
        this.style.transform = '';
      }, 100);
    });
  });
}

/**
 * 레벨 계산 및 표시 함수 (my-page.js에서 가져옴, 필요시 사용)
 */
function calculateLevel(prestige) {
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

/**
 * 레벨에 따른 별명 반환
 */
function getLevelTitle(level) {
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

  if (level < 0) {
    return levelTitles[0];
  }
  if (level >= levelTitles.length) {
    return levelTitles[levelTitles.length - 1];
  }
  return levelTitles[level];
}