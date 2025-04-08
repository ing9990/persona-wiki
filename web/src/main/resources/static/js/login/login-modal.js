/**
 * 로그인 모달 관리 스크립트
 * 페이지 내에서 로그인 모달을 열고 닫는 기능을 제공합니다.
 */
document.addEventListener('DOMContentLoaded', function () {
  // 모달 요소
  const loginModal = document.getElementById('loginModal');
  const loginModalContent = document.getElementById('loginModalContent');
  const closeLoginModalBtn = document.getElementById('closeLoginModal');
  const continueWithoutLoginBtn = document.getElementById(
      'continueWithoutLogin');

  // 로그인 필요한 요소들 (class="requires-login"이 있는 모든 요소)
  const requiresLoginElements = document.querySelectorAll('.requires-login');
  // 직접 로그인 버튼
  const loginButtons = document.querySelectorAll('.login-button');

  // 로그인 상태 확인
  const isLoggedIn = document.getElementById('userLoginStatus')?.value
      === 'true';

  // 로그인 모달 열기 - 전역 함수로 등록
  window.openLoginModal = function () {
    if (isLoggedIn) {
      return;
    } // 이미 로그인된 경우 모달 열지 않음

    // 먼저 모달을 보이게 설정하고, 애니메이션을 위한 클래스 제거
    loginModal.classList.remove('hidden');

    // FOUC(Flash of Unstyled Content) 방지를 위해 requestAnimationFrame 사용
    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        // 배경 오버레이 페이드인
        loginModal.classList.add('bg-opacity-50');
        // 콘텐츠 확대 및 페이드인
        loginModalContent.classList.remove('scale-95', 'opacity-0');
        loginModalContent.classList.add('scale-100', 'opacity-100');
      });
    });

    // 포커스 트랩 및 접근성
    loginModal.setAttribute('aria-hidden', 'false');
    document.body.classList.add('overflow-hidden'); // 배경 스크롤 방지
  };

  // 로그인 모달 닫기 - 전역 함수로 등록
  window.closeLoginModal = function () {
    // 모달 콘텐츠 애니메이션 실행
    loginModalContent.classList.remove('scale-100', 'opacity-100');
    loginModalContent.classList.add('scale-95', 'opacity-0');

    // 배경 오버레이 애니메이션 실행 (동시에)
    loginModal.classList.add('bg-opacity-0');
    loginModal.classList.remove('bg-opacity-50');

    // 애니메이션 완료 후 숨김 처리
    setTimeout(() => {
      loginModal.classList.add('hidden');
      loginModal.setAttribute('aria-hidden', 'true');
      document.body.classList.remove('overflow-hidden');
    }, 300); // 애니메이션 지속 시간과 일치
  };

  // 이벤트 리스너 등록
  if (closeLoginModalBtn) {
    closeLoginModalBtn.addEventListener('click', window.closeLoginModal);
  }

  if (continueWithoutLoginBtn) {
    continueWithoutLoginBtn.addEventListener('click', function () {
      window.closeLoginModal();
      // 현재 URL에 리디렉션 파라미터가 있으면 해당 페이지로 이동
      const urlParams = new URLSearchParams(window.location.search);
      const redirectUrl = urlParams.get('redirect');
      if (redirectUrl) {
        window.location.href = redirectUrl;
      }
    });
  }

  // 로그인 버튼 클릭 시 모달 열기
  loginButtons.forEach(button => {
    button.addEventListener('click', function (e) {
      e.preventDefault();
      window.openLoginModal();
    });
  });

  // 로그인 필요한 기능 클릭 시 모달 열기
  requiresLoginElements.forEach(element => {
    element.addEventListener('click', function (e) {
      if (!isLoggedIn) {
        e.preventDefault();
        window.openLoginModal();
      }
    });
  });

  // 모달 외부 클릭 시 닫기
  loginModal?.addEventListener('click', function (e) {
    if (e.target === loginModal) {
      window.closeLoginModal();
    }
  });

  // ESC 키 누를 때 모달 닫기
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape' && !loginModal?.classList.contains('hidden')) {
      window.closeLoginModal();
    }
  });

  // URL에 login=required 파라미터가 있으면 모달 자동 열기
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get('login') === 'required') {
    window.openLoginModal();
  }
});