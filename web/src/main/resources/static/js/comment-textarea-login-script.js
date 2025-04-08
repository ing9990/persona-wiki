/**
 * 댓글 textarea 로그인 처리 스크립트
 * 비로그인 사용자가 댓글 입력창을 클릭/포커스할 때 로그인 모달을 표시합니다.
 */
document.addEventListener('DOMContentLoaded', function () {
  // 로그인 상태 확인
  const isLoggedIn = document.getElementById('userLoginStatus')?.value
      === 'true';

  // 로그인되지 않은 상태에서만 처리
  if (!isLoggedIn) {
    // 모든 comment-login-trigger 클래스를 가진 요소에 이벤트 리스너 추가
    const commentTriggers = document.querySelectorAll('.comment-login-trigger');

    commentTriggers.forEach(trigger => {
      // 읽기 전용으로 설정 (입력 방지)
      trigger.setAttribute('readonly', 'readonly');

      // 클릭 및 포커스 이벤트에 로그인 모달 표시
      trigger.addEventListener('click', handleLoginTrigger);
      trigger.addEventListener('focus', handleLoginTrigger);
    });

    // 동적으로 추가되는 답글창도 처리 (예: 답글 버튼 클릭 시 생성되는 입력창)
    document.addEventListener('DOMNodeInserted', function (e) {
      if (e.target.classList && e.target.classList.contains(
          'comment-login-trigger')) {
        e.target.setAttribute('readonly', 'readonly');
        e.target.addEventListener('click', handleLoginTrigger);
        e.target.addEventListener('focus', handleLoginTrigger);
      }
    });
  }

  // 로그인 모달 표시 핸들러
  function handleLoginTrigger(e) {
    e.preventDefault();

    // 전역 openLoginModal 함수 호출
    if (typeof window.openLoginModal === 'function') {
      window.openLoginModal();
    } else {
      // login-modal.js에서 전역 함수를 제공하지 않는 경우
      // login-button 클래스를 가진 요소 클릭 이벤트 시뮬레이션
      const loginButton = document.querySelector('.login-button');
      if (loginButton) {
        loginButton.click();
      }
    }

    // 포커스 해제
    this.blur();
  }
});