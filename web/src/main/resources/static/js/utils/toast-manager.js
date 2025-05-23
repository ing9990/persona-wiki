/**
 * 토스트 알림 관리자
 * 각종 알림을 화면 우하단에 표시하는 기능 제공
 */
class ToastManager {
  constructor() {
    this.container = null;
    this.toasts = [];
    this.queue = [];
    this.maxToasts = 3;
    this.defaultDuration = 3000;
    this._initialized = false;

    // DOMContentLoaded 이벤트에서 초기화
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', () => this.init());
    } else {
      // 이미 DOM이 로드된 경우 즉시 초기화
      this.init();
    }
  }

  /**
   * 토스트 컨테이너 초기화
   * @private
   */
  init() {
    // 이미 초기화되었다면 중복 실행 방지
    if (this._initialized) {
      return;
    }

    // 기존 컨테이너가 있는지 확인
    let container = document.getElementById('toast-container');

    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.className = 'fixed bottom-4 right-4 z-50 flex flex-col gap-2 items-end';
      document.body.appendChild(container);
    }

    this.container = container;

    // 스타일 추가
    this.addStyles();

    // 초기화 완료 표시
    this._initialized = true;
    console.info('[ToastManager] 초기화 완료');
  }

  /**
   * 토스트 알림에 필요한 스타일 추가
   * @private
   */
  addStyles() {
    const styleId = 'toast-manager-styles';

    // 이미 스타일이 추가되어 있는지 확인
    if (document.getElementById(styleId)) {
      return;
    }

    const style = document.createElement('style');
    style.id = styleId;
    style.textContent = `
      .toast {
        transition: all 0.3s ease;
        max-width: 400px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .toast-enter {
        transform: translateX(100%);
        opacity: 0;
      }
      .toast-visible {
        transform: translateX(0);
        opacity: 1;
      }
      .toast-exit {
        transform: translateX(100%);
        opacity: 0;
      }
      .toast-success {
        background-color: #10B981;
        color: white;
      }
      .toast-error {
        background-color: #EF4444;
        color: white;
      }
      .toast-warning {
        background-color: #F59E0B;
        color: white;
      }
      .toast-info {
        background-color: #3B82F6;
        color: white;
      }
    `;

    document.head.appendChild(style);
  }

  /**
   * 토스트 생성 공통 함수
   * @param {string} message - 표시할 메시지
   * @param {string} type - 토스트 유형 (success, error, warning, info)
   * @param {number} [duration] - 토스트 표시 시간 (ms)
   * @private
   */
  createToast(message, type, duration = this.defaultDuration) {
    // 초기화 확인
    if (!this._initialized) {
      this.init();
    }

    const toastElement = document.createElement('div');
    toastElement.className = `toast toast-${type} px-4 py-2 rounded-md toast-enter flex items-center justify-between`;

    // 아이콘 선택
    let icon = '';
    switch (type) {
      case 'success':
        icon = '<i class="fas fa-check-circle mr-2"></i>';
        break;
      case 'error':
        icon = '<i class="fas fa-exclamation-circle mr-2"></i>';
        break;
      case 'warning':
        icon = '<i class="fas fa-exclamation-triangle mr-2"></i>';
        break;
      case 'info':
        icon = '<i class="fas fa-info-circle mr-2"></i>';
        break;
    }

    // 닫기 버튼
    const closeButton = '<button class="ml-3 text-white focus:outline-none"><i class="fas fa-times"></i></button>';

    toastElement.innerHTML = `
      <div class="flex items-center">${icon}<span>${message}</span></div>
      ${closeButton}
    `;

    // 닫기 버튼 이벤트
    toastElement.querySelector('button').addEventListener('click', () => {
      this.removeToast(toastElement);
    });

    return {toast: toastElement, duration};
  }

  /**
   * 토스트 표시
   * @param {HTMLElement} toast - 토스트 요소
   * @param {number} duration - 표시 시간
   * @private
   */
  showToast(toast, duration) {
    // 초기화 확인
    if (!this._initialized) {
      this.init();
    }

    // 현재 표시 중인 토스트가 최대 개수보다 적으면 바로 표시
    if (this.toasts.length < this.maxToasts) {
      this.container.appendChild(toast);
      this.toasts.push(toast);

      // 애니메이션을 위해 약간의 딜레이 추가
      setTimeout(() => {
        toast.classList.replace('toast-enter', 'toast-visible');
      }, 10);

      // 지정된 시간 후 제거
      setTimeout(() => {
        this.removeToast(toast);
      }, duration);
    } else {
      // 대기열에 추가
      this.queue.push({toast, duration});
    }
  }

  /**
   * 토스트 제거
   * @param {HTMLElement} toast - 제거할 토스트 요소
   * @private
   */
  removeToast(toast) {
    // 이미 제거 중인지 확인
    if (toast.classList.contains('toast-exit')) {
      return;
    }

    // 제거 애니메이션
    toast.classList.replace('toast-visible', 'toast-exit');

    setTimeout(() => {
      // 배열에서 제거
      const index = this.toasts.indexOf(toast);
      if (index > -1) {
        this.toasts.splice(index, 1);
      }

      // DOM에서 제거
      if (toast.parentNode) {
        toast.parentNode.removeChild(toast);
      }

      // 대기 중인 토스트가 있으면 표시
      if (this.queue.length > 0) {
        const next = this.queue.shift();
        this.showToast(next.toast, next.duration);
      }
    }, 300); // 애니메이션 시간
  }

  /**
   * 성공 토스트 표시
   * @param {string} message - 표시할 메시지
   * @param {number} [duration] - 표시 시간 (ms)
   */
  success(message, duration) {
    const {toast, duration: d} = this.createToast(message, 'success', duration);
    this.showToast(toast, d);
  }

  /**
   * 에러 토스트 표시
   * @param {string} message - 표시할 메시지
   * @param {number} [duration] - 표시 시간 (ms)
   */
  error(message, duration) {
    const {toast, duration: d} = this.createToast(message, 'error', duration);
    this.showToast(toast, d);
  }

  /**
   * 경고 토스트 표시
   * @param {string} message - 표시할 메시지
   * @param {number} [duration] - 표시 시간 (ms)
   */
  warning(message, duration) {
    const {toast, duration: d} = this.createToast(message, 'warning', duration);
    this.showToast(toast, d);
  }

  /**
   * 정보 토스트 표시
   * @param {string} message - 표시할 메시지
   * @param {number} [duration] - 표시 시간 (ms)
   */
  info(message, duration) {
    const {toast, duration: d} = this.createToast(message, 'info', duration);
    this.showToast(toast, d);
  }
}

// 싱글톤 인스턴스 생성 - 변수명 변경
const toastManager = new ToastManager();

// 전역으로 노출 - 변수명 변경
window.toastManager = toastManager;