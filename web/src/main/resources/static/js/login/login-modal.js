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

  /**
   * 동적으로 추가되는 요소를 포함하여 모든 로그인 필요 요소에 이벤트 등록
   */
  function setupLoginRequiredElements() {
    // 직접 로그인 버튼
    const loginButtons = document.querySelectorAll('.login-button');

    // 로그인 버튼 클릭 시 모달 열기
    loginButtons.forEach(button => {
      button.addEventListener('click', function (e) {
        e.preventDefault();
        window.openLoginModal();
      });
    });

    // 이미 존재하는 로그인 필요 요소에 이벤트 추가
    setupExistingLoginRequiredElements();

    // 동적으로 추가되는 요소를 위한 MutationObserver 설정
    setupMutationObserver();
  }

  /**
   * 이미 존재하는 로그인 필요 요소에 이벤트 추가
   */
  function setupExistingLoginRequiredElements() {
    // 로그인 필요한 기능 클릭 시 모달 열기
    const requiresLoginElements = document.querySelectorAll('.requires-login');
    requiresLoginElements.forEach(element => {
      // 이미 이벤트가 등록되어 있으면 건너뜀
      if (element.hasAttribute('data-login-check-applied')) {
        return;
      }

      element.setAttribute('data-login-check-applied', 'true');

      if (!isLoggedIn) {
        element.addEventListener('click', function (e) {
          // submit 버튼인 경우 폼 제출 방지
          if (element.type === 'submit') {
            e.preventDefault();
          }
          window.openLoginModal();
        });

        element.addEventListener('focus', function (e) {
          window.openLoginModal();
          // 포커스 해제
          element.blur();
        });
      }
    });
  }

  /**
   * 동적으로 추가되는 요소를 감시하는 MutationObserver 설정
   */
  function setupMutationObserver() {
    // 이미 로그인된 상태면 불필요
    if (isLoggedIn) {
      return;
    }

    const observer = new MutationObserver(function (mutations) {
      mutations.forEach(function (mutation) {
        if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
          // 새로 추가된 DOM 요소에서 로그인 필요 요소 찾기
          mutation.addedNodes.forEach(function (node) {
            if (node.nodeType === 1) { // Element 노드인 경우만
              // 요소 자체가 로그인 필요 요소인 경우
              if (node.classList && node.classList.contains('requires-login') &&
                  !node.hasAttribute('data-login-check-applied')) {
                applyLoginCheckToElement(node);
              }

              // 요소 내부에 로그인 필요 요소가 있는 경우
              const loginElements = node.querySelectorAll ?
                  node.querySelectorAll('.requires-login') : [];

              loginElements.forEach(element => {
                if (!element.hasAttribute('data-login-check-applied')) {
                  applyLoginCheckToElement(element);
                }
              });
            }
          });
        }
      });
    });

    // body 전체를 관찰
    observer.observe(document.body, {
      childList: true,
      subtree: true
    });
  }

  /**
   * 요소에 로그인 체크 이벤트 적용
   */
  function applyLoginCheckToElement(element) {
    element.setAttribute('data-login-check-applied', 'true');

    element.addEventListener('click', function (e) {
      // submit 버튼인 경우 폼 제출 방지
      if (element.type === 'submit') {
        e.preventDefault();
      }
      window.openLoginModal();
    });

    element.addEventListener('focus', function (e) {
      window.openLoginModal();
      element.blur();
    });
  }

  // 로그인 필요 요소 설정 실행
  setupLoginRequiredElements();

  // URL에 login=required 파라미터가 있으면 모달 자동 열기
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get('login') === 'required') {
    window.openLoginModal();
  }
});