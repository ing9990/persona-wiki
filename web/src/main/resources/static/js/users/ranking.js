/**
 * 사용자 랭킹 페이지 자바스크립트
 */
document.addEventListener('DOMContentLoaded', function () {
  // 스크롤 애니메이션
  initScrollAnimations();

  // 프로필 이미지 호버 효과
  initProfileImageHover();

  // 현재 사용자 행으로 스크롤
  scrollToCurrentUser();

  // 반응형 테이블 최적화
  optimizeTableForMobile();

  // 페이지네이션 관련 이벤트
  initPaginationEvents();
});

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
  const tableRows = document.querySelectorAll('tbody tr');
  tableRows.forEach((row, index) => {
    row.classList.add('animate-on-scroll');
    row.style.transitionDelay = `
$
{
  0.1 + (index * 0.05)
}
s`;
  });

  // 초기 애니메이션 트리거
  triggerScrollAnimations();
}

/**
 * 프로필 이미지 호버 효과 초기화
 */
function initProfileImageHover() {
  const profileImages = document.querySelectorAll('table img');

  profileImages.forEach(img => {
    img.classList.add('profile-image-hover');
    img.parentElement.classList.add('overflow-hidden');

    // 이미지 로드 오류 시 기본 이미지 설정
    img.addEventListener('error', function () {
      this.src = '/img/profile-placeholder.svg';
    });
  });

  // Top 3 프로필 이미지에 특별 효과
  const topProfileImages = document.querySelectorAll('.rank-card img');
  topProfileImages.forEach(img => {
    img.addEventListener('mouseenter', function () {
      this.parentElement.style.transform = 'scale(1.1)';
      this.parentElement.style.transition = 'transform 0.3s ease';
    });

    img.addEventListener('mouseleave', function () {
      this.parentElement.style.transform = 'scale(1)';
    });
  });
}

/**
 * 현재 사용자 행으로 스크롤
 */
function scrollToCurrentUser() {
  const currentUserRow = document.querySelector('tbody tr.bg-indigo-50');

  if (currentUserRow) {
    // 0.5초 후 스크롤 (페이지 로드 후)
    setTimeout(() => {
      const offset = currentUserRow.offsetTop - window.innerHeight / 2;

      // 부드러운 스크롤 적용
      window.scrollTo({
        top: offset,
        behavior: 'smooth'
      });

      // 잠깐 동안 행 강조
      currentUserRow.classList.add('transition-all', 'duration-500');
      currentUserRow.style.backgroundColor = 'rgba(99, 102, 241, 0.2)';

      setTimeout(() => {
        currentUserRow.style.backgroundColor = '';
      }, 1500);
    }, 500);
  }
}

/**
 * 반응형 테이블 최적화
 */
function optimizeTableForMobile() {
  // 화면 크기에 따라 테이블 칼럼 표시/숨김
  function adjustTable() {
    const isMobile = window.innerWidth < 768;
    const levelColumns = document.querySelectorAll(
        'th:nth-child(3), td:nth-child(3)');
    const bioColumns = document.querySelectorAll(
        'th:nth-child(5), td:nth-child(5)');

    levelColumns.forEach(col => {
      col.style.display = isMobile ? 'none' : '';
    });

    bioColumns.forEach(col => {
      col.style.display = isMobile ? 'none' : '';
    });
  }

  // 초기 실행
  adjustTable();

  // 화면 크기 변경 시 실행
  window.addEventListener('resize', adjustTable);
}

/**
 * 페이지네이션 관련 이벤트 초기화
 */
function initPaginationEvents() {
  const paginationLinks = document.querySelectorAll('.pagination-link');

  paginationLinks.forEach(link => {
    // 호버 효과
    link.addEventListener('mouseenter', function () {
      if (!this.classList.contains('pointer-events-none')) {
        this.classList.add('transform', 'scale-105');
      }
    });

    link.addEventListener('mouseleave', function () {
      this.classList.remove('transform', 'scale-105');
    });

    // 클릭 효과
    link.addEventListener('click', function (e) {
      if (this.classList.contains('pointer-events-none')) {
        e.preventDefault();
        return false;
      }

      this.classList.add('transform', 'scale-95');
      setTimeout(() => {
        this.classList.remove('transform', 'scale-95');
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

// 레벨에 따른 별명 반환
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
