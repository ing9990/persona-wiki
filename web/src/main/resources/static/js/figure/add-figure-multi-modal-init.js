/**
 * 인물 추가 다단계 모달 초기화 스크립트
 * 모달 UI 컴포넌트와 핸들러를 연결하고 초기화합니다.
 */
(function () {
  // 모듈 간 의존성 확인
  if (!window.FigureModalUI) {
    console.error(
        'FigureModalUI가 정의되지 않았습니다. add-figure-multi-modal-ui.js를 먼저 로드하세요.');
    return;
  }

  /**
   * 전역 인물 추가 모달 모듈
   */
  class FigureModalModule {
    constructor() {
      this.ui = new FigureModalUI();
      this.handler = null;

      // DOM이 로드된 후 핸들러 초기화
      if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => this.initHandler());
      } else {
        this.initHandler();
      }
    }

    /**
     * 핸들러 초기화
     * @private
     */
    initHandler() {
      // 핸들러 스크립트 로드 확인
      if (window.FigureModalHandler) {
        this.handler = new FigureModalHandler(this.ui);
        this.initModal();
      } else {
        console.error(
            'FigureModalHandler가 정의되지 않았습니다. add-figure-multi-modal-handler.js를 로드하세요.');

        // 핸들러 스크립트 자동 로드 시도
        this.loadHandlerScript(() => {
          this.handler = new FigureModalHandler(this.ui);
          this.initModal();
        });
      }
    }

    /**
     * 핸들러 스크립트 동적 로드
     * @param {Function} callback - 로드 완료 후 콜백
     * @private
     */
    loadHandlerScript(callback) {
      const script = document.createElement('script');
      script.src = '/js/add-figure-multi-modal-handler.js';
      script.onload = callback;
      script.onerror = () => console.error('핸들러 스크립트 로드에 실패했습니다.');
      document.head.appendChild(script);
    }

    /**
     * 모달 초기화
     * @private
     */
    initModal() {
      console.info('[FigureModal] 초기화 완료');

      // 전역 함수로 모달 열기 함수 노출
      window.openFigureModal = () => {
        if (this.handler) {
          this.handler.openModal();
        } else {
          console.error('핸들러가 초기화되지 않았습니다.');
        }
      };
    }
  }

  // 싱글톤 인스턴스 생성
  window.figureModal = new FigureModalModule();
})();