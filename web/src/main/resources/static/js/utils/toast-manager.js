/**
 * 토스트 메시지 관리자
 * 사용자에게 간단한 알림 메시지를 표시합니다.
 */
class ToastManager {
  /**
   * 토스트 메시지 표시
   * @param {string} message - 표시할 메시지
   * @param {boolean} [isError=false] - 에러 메시지 여부
   */
  static show(message, isError = false) {
    // 기존 토스트 메시지 제거
    const existingToast = document.querySelector('.toast-message');
    if (existingToast) {
      existingToast.remove();
    }

    // 새 토스트 메시지 생성
    const toast = document.createElement('div');
    toast.className = `fixed bottom-4 right-4 py-2 px-4 rounded-lg shadow-lg z-50 toast-message transition-opacity duration-300 ${isError
        ? 'bg-red-600' : 'bg-indigo-600'} text-white`;
    toast.textContent = message;

    // 문서에 추가
    document.body.appendChild(toast);

    // 3초 후 제거
    setTimeout(() => {
      toast.style.opacity = '0';
      setTimeout(() => {
        toast.remove();
      }, 300);
    }, 3000);
  }
}

// 전역 범위에서 접근 가능하도록 노출
window.ToastManager = ToastManager;