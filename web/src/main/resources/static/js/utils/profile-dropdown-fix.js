// profile-dropdown-fix.js

document.addEventListener('DOMContentLoaded', function() {
  // Alpine.js가 로드된 후에 실행되도록 지연
  setTimeout(() => {
    // 페이지가 figure-list-by-category.html인 경우 확인
    const isFigureListPage = window.location.pathname.includes('/figures') ||
        document.title.includes('인물 목록');

    if (isFigureListPage) {
      // Alpine.js 컴포넌트 재설정
      const profileDropdowns = document.querySelectorAll('[x-data*="open"]');
      profileDropdowns.forEach(dropdown => {
        // Alpine.js 상태 재설정
        if (window.Alpine) {
          const alpineComponent = Alpine.$data(dropdown);
          if (alpineComponent && typeof alpineComponent.open !== 'undefined') {
            alpineComponent.open = false;
          }
        }
      });
    }
  }, 100); // Alpine.js가 초기화된 후 실행되도록 약간의 지연 추가
});