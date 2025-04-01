document.addEventListener('DOMContentLoaded', function () {
  // 모달 관련 DOM
  const modal = document.getElementById('welcomeModal');
  const modalContent = document.getElementById('welcomeModalContent');
  const overlay = document.getElementById('welcomeModalOverlay');
  const closeIcon = document.getElementById('welcomeModalCloseIcon');
  const closeBtn = document.getElementById('welcomeModalCloseBtn');
  const optOutBtn = document.getElementById('welcomeModalOptOutBtn');
  const welcomeTextContainer = document.getElementById('welcomeModalText');

  // "다시 보지 않기" 여부 (localStorage)
  const hasOptOut = localStorage.getItem('welcomeModalOptOut') === 'true';

  /**
   * 단락을 3개로 나누고, 중요한 부분은 <strong>으로 bold 처리
   * (각 요소 = 한 단락)
   */
  const disclaimers = [
    "⭐️ <strong>숭배와 사형</strong>은 의견 스펙트럼을 재미있게 표현한 것입니다.",
    "❌ 이 사이트의 <strong>모든 정보는 사용자들이 공유</strong>하는 것이며, <strong>일부 내용은 부정확</strong>할 수 있습니다.",
    "✅ <strong>명예훼손 또는 심한 비방 내용은 사전 안내 없이 삭제</strong>될 수 있습니다."
  ];

  // 페이지 로드 시, "다시 보지 않기"가 설정되어 있지 않다면 모달 표시
  if (!hasOptOut) {
    openWelcomeModal();
  }

  /**
   * 모달 열기
   */
  function openWelcomeModal() {
    // 텍스트 영역 초기화
    welcomeTextContainer.innerHTML = "";

    // 모달 Fade-In
    modal.classList.remove('pointer-events-none', 'opacity-0');
    modalContent.classList.remove('scale-95', 'opacity-0');
    modal.setAttribute('aria-hidden', 'false');

    // 버튼들 숨기기 (안내 문구 끝까지 보여준 후 노출)
    closeBtn.classList.add('hidden');
    optOutBtn.classList.add('hidden');

    // 한 줄(단락)씩 Fade-In
    showLinesOneByOne(disclaimers, welcomeTextContainer, () => {
      // 모든 단락 표시 완료 후 버튼 보이기
      closeBtn.classList.remove('hidden');
      optOutBtn.classList.remove('hidden');
    });
  }

  /**
   * 모달 닫기
   */
  function closeWelcomeModal() {
    // Fade-Out
    modal.classList.add('pointer-events-none', 'opacity-0');
    modalContent.classList.add('scale-95', 'opacity-0');
    modal.setAttribute('aria-hidden', 'true');
  }

  // 닫기 아이콘(X) 클릭
  closeIcon.addEventListener('click', closeWelcomeModal);
  // "이해했습니다" 버튼
  closeBtn.addEventListener('click', closeWelcomeModal);
  // "다시 보지 않기" 버튼
  optOutBtn.addEventListener('click', function () {
    localStorage.setItem('welcomeModalOptOut', 'true');
    closeWelcomeModal();
  });
  // 배경(overlay) 클릭
  overlay.addEventListener('click', function (e) {
    if (e.target === overlay) {
      closeWelcomeModal();
    }
  });
});

/**
 * 배열의 각 요소(단락)을 한 줄씩 Fade-In해주는 함수
 * - lines: 문구 배열 (HTML 태그 포함 가능)
 * - container: 출력될 DOM 컨테이너
 * - onComplete: 모든 라인 표시 후 호출되는 콜백
 */
function showLinesOneByOne(lines, container, onComplete) {
  let currentLineIndex = 0;

  function showNextLine() {
    if (currentLineIndex >= lines.length) {
      if (onComplete) {
        onComplete();
      }
      return;
    }

    // 새 div 생성
    const lineEl = document.createElement('div');
    // innerHTML로 주입해야 bold 태그가 살아있음
    lineEl.innerHTML = lines[currentLineIndex];
    currentLineIndex++;

    // 초기 상태: 투명 + 약간 아래로 이동
    lineEl.classList.add(
        "opacity-0",
        "translate-y-2",
        "transition-all",
        "duration-300",
        "ease-out",
        "mb-4"
    );
    container.appendChild(lineEl);

    // requestAnimationFrame을 써서, 다음 렌더 사이클에 Fade-In
    requestAnimationFrame(() => {
      lineEl.classList.remove("opacity-0", "translate-y-2");
    });

    // 0.6초 뒤에 다음 라인 표시
    setTimeout(showNextLine, 600);
  }

  showNextLine();
}
