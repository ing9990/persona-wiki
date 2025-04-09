/**
 * 국민사형투표 공통 JavaScript 파일
 * 작성일: 2025-03-30
 *
 * 모든 페이지에서 공통으로 사용되는 기능을 초기화합니다.
 */

document.addEventListener('DOMContentLoaded', function () {

  // 공통 초기화 함수 실행
  initNavActiveState();
  setupScrollToTopButton();
  setupDropdowns();
  setupMobileMenu();
  initImageErrorHandling();

  // 툴팁 초기화 (있는 경우)
  if (typeof initTooltips === 'function') {
    initTooltips();
  }
});

/**
 * 현재 페이지에 해당하는 네비게이션 링크 활성화
 */
function initNavActiveState() {
  const currentPath = window.location.pathname;
  const navLinks = document.querySelectorAll('.navbar-nav .nav-link');

  navLinks.forEach(link => {
    const href = link.getAttribute('href');

    // 정확한 경로 매칭
    if (href === currentPath) {
      link.classList.add('active');
    }
    // 하위 경로 포함 매칭 (예: /figures로 시작하는 모든 경로)
    else if (href !== '/' && currentPath.startsWith(href)) {
      link.classList.add('active');
    }
  });
}

/**
 * 스크롤 상단으로 버튼 설정
 */
function setupScrollToTopButton() {
  // 스크롤 버튼이 존재하는지 확인
  const scrollButton = document.getElementById('scrollToTopButton');
  if (!scrollButton) {
    return;
  }

  // 초기에는 버튼 숨김
  scrollButton.classList.add('hidden');

  // 스크롤 이벤트 리스너
  window.addEventListener('scroll', function () {
    if (window.scrollY > 300) {
      scrollButton.classList.remove('hidden');
    } else {
      scrollButton.classList.add('hidden');
    }
  });

  // 클릭 이벤트 리스너
  scrollButton.addEventListener('click', function () {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  });
}

/**
 * 드롭다운 메뉴 설정
 */
function setupDropdowns() {
  const dropdownButtons = document.querySelectorAll('[data-dropdown-toggle]');

  dropdownButtons.forEach(button => {
    const targetId = button.getAttribute('data-dropdown-toggle');
    const target = document.getElementById(targetId);

    if (!target) {
      return;
    }

    // 드롭다운 토글 이벤트
    button.addEventListener('click', function (e) {
      e.stopPropagation();

      // 현재 드롭다운이 열려있으면 닫기
      const isOpen = !target.classList.contains('hidden');

      // 다른 열린 드롭다운을 모두 닫기
      document.querySelectorAll('[data-dropdown]').forEach(dropdown => {
        if (dropdown.id !== targetId) {
          dropdown.classList.add('hidden');
        }
      });

      // 현재 드롭다운 토글
      target.classList.toggle('hidden', isOpen);
    });
  });

  // 문서 클릭 시 열린 드롭다운 모두 닫기
  document.addEventListener('click', function () {
    document.querySelectorAll('[data-dropdown]').forEach(dropdown => {
      dropdown.classList.add('hidden');
    });
  });

  // 드롭다운 내부 클릭 시 이벤트 전파 중지
  document.querySelectorAll('[data-dropdown]').forEach(dropdown => {
    dropdown.addEventListener('click', function (e) {
      e.stopPropagation();
    });
  });
}

/**
 * 모바일 메뉴 설정
 */
function setupMobileMenu() {
  const mobileMenuButton = document.getElementById('mobileMenuButton');
  const mobileMenu = document.getElementById('mobileMenu');

  if (!mobileMenuButton || !mobileMenu) {
    return;
  }

  mobileMenuButton.addEventListener('click', function () {
    mobileMenu.classList.toggle('hidden');
  });
}

/**
 * 이미지 오류 처리 초기화
 */
function initImageErrorHandling() {
  // 카테고리 이미지 오류 처리
  document.querySelectorAll('.category-image, .category-header-image').forEach(
      img => {
        img.onerror = function () {
          this.src = '/img/category-placeholder.jpg';
        };
      });

  // 인물 이미지 오류 처리
  document.querySelectorAll(
      '.figure-image, .profile-header-image, .profile-image').forEach(img => {
    img.onerror = function () {
      this.src = '/img/profile-placeholder.svg';
    };
  });
}

/**
 * 툴팁 초기화
 */
function initTooltips() {
  const tooltipTriggerList = document.querySelectorAll('[data-tooltip]');
  tooltipTriggerList.forEach(tooltipTriggerEl => {
    const tooltip = tooltipTriggerEl.getAttribute('data-tooltip');

    if (!tooltip) {
      return;
    }

    // 마우스 오버 시 툴팁 표시
    tooltipTriggerEl.addEventListener('mouseenter', function (e) {
      // 기존 툴팁 제거
      const existingTooltip = document.querySelector('.tooltip-popup');
      if (existingTooltip) {
        existingTooltip.remove();
      }

      // 새 툴팁 생성
      const tooltipEl = document.createElement('div');
      tooltipEl.className = 'tooltip-popup absolute bg-gray-800 text-white text-xs px-2 py-1 rounded z-50 whitespace-nowrap';
      tooltipEl.textContent = tooltip;

      // 툴팁 위치 설정
      document.body.appendChild(tooltipEl);
      const rect = tooltipTriggerEl.getBoundingClientRect();
      const tooltipRect = tooltipEl.getBoundingClientRect();

      tooltipEl.style.top = (rect.top - tooltipRect.height - 5) + 'px';
      tooltipEl.style.left = (rect.left + (rect.width / 2) - (tooltipRect.width
          / 2)) + 'px';

      // 화면 밖으로 나가지 않도록 조정
      if (parseFloat(tooltipEl.style.left) < 0) {
        tooltipEl.style.left = '0px';
      } else if (parseFloat(tooltipEl.style.left) + tooltipRect.width
          > window.innerWidth) {
        tooltipEl.style.left = (window.innerWidth - tooltipRect.width) + 'px';
      }
    });

    // 마우스 아웃 시 툴팁 제거
    tooltipTriggerEl.addEventListener('mouseleave', function () {
      const tooltip = document.querySelector('.tooltip-popup');
      if (tooltip) {
        tooltip.remove();
      }
    });
  });
}