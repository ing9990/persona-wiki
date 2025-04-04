/**
 * 국민사형투표 공통 JavaScript 유틸리티
 * 작성일: 2025-04-05
 *
 * 여러 페이지에서 공통적으로 사용되는 유틸리티 함수들을 모아놓은 파일입니다.
 */

// 즉시 실행 함수로 전역 네임스페이스 오염 방지
const CommonUtils = (function () {
  /**
   * 이미지 오류 처리 유틸리티
   * 이미지 로드 실패 시 기본 이미지로 대체
   */
  const ImageUtils = {
    /**
     * 이미지 오류 처리 설정
     * @param {string} selector - 대상 이미지 선택자
     * @param {string} fallbackUrl - 기본 이미지 URL
     */
    setupErrorHandling: function (selector = 'img[data-handle-error="true"]',
        fallbackUrl = '/img/profile-placeholder.svg') {
      document.querySelectorAll(selector).forEach(img => {
        img.addEventListener('error', () => {
          const placeholderSrc = img.getAttribute('data-placeholder')
              || fallbackUrl;
          img.src = placeholderSrc;
        });
      });
    },

    /**
     * 특정 이미지에 오류 핸들러 추가
     * @param {HTMLImageElement} img - 이미지 요소
     * @param {string} fallbackSrc - 대체 이미지 경로
     */
    handleImageError: function (img,
        fallbackSrc = '/img/profile-placeholder.svg') {
      img.onerror = null; // 무한 루프 방지
      img.src = fallbackSrc;
    }
  };

  /**
   * DOM 조작 유틸리티
   */
  const DOMUtils = {
    /**
     * 모달 창 생성 및 제어
     * @param {Object} options - 모달 옵션
     * @returns {Object} - 모달 제어 객체
     */
    createModal: function (options) {
      const defaultOptions = {
        id: 'modal',
        title: '알림',
        content: '',
        confirmText: '확인',
        cancelText: '취소',
        onConfirm: null,
        onCancel: null,
        showCancel: true
      };

      const settings = {...defaultOptions, ...options};

      // 기존 모달 제거
      const existingModal = document.getElementById(settings.id);
      if (existingModal) {
        existingModal.remove();
      }

      // 모달 DOM 생성
      const modal = document.createElement('div');
      modal.id = settings.id;
      modal.className = 'fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center hidden';
      modal.innerHTML = `
        <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 overflow-hidden">
          <div class="flex items-center justify-between p-4 border-b">
            <h3 class="text-lg font-medium text-gray-900">${settings.title}</h3>
            <button type="button" class="modal-close text-gray-400 hover:text-gray-500">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="p-4">
            ${settings.content}
          </div>
          <div class="px-4 py-3 bg-gray-50 text-right space-x-2">
            ${settings.showCancel ?
          `<button type="button" class="modal-cancel px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">
                ${settings.cancelText}
              </button>` : ''}
            <button type="button" class="modal-confirm px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700">
              ${settings.confirmText}
            </button>
          </div>
        </div>
      `;

      document.body.appendChild(modal);

      // 이벤트 핸들러 설정
      const closeBtn = modal.querySelector('.modal-close');
      const confirmBtn = modal.querySelector('.modal-confirm');
      const cancelBtn = modal.querySelector('.modal-cancel');

      closeBtn.addEventListener('click', () => {
        this.hideModal(modal);
        if (settings.onCancel) {
          settings.onCancel();
        }
      });

      confirmBtn.addEventListener('click', () => {
        this.hideModal(modal);
        if (settings.onConfirm) {
          settings.onConfirm();
        }
      });

      if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
          this.hideModal(modal);
          if (settings.onCancel) {
            settings.onCancel();
          }
        });
      }

      // 모달 외부 클릭 이벤트
      modal.addEventListener('click', (e) => {
        if (e.target === modal) {
          this.hideModal(modal);
          if (settings.onCancel) {
            settings.onCancel();
          }
        }
      });

      return {
        show: () => this.showModal(modal),
        hide: () => this.hideModal(modal),
        getElement: () => modal
      };
    },

    /**
     * 모달 표시
     * @param {HTMLElement} modal - 모달 요소
     */
    showModal: function (modal) {
      modal.classList.remove('hidden');
      document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
    },

    /**
     * 모달 숨김
     * @param {HTMLElement} modal - 모달 요소
     */
    hideModal: function (modal) {
      modal.classList.add('hidden');
      document.body.style.overflow = ''; // 배경 스크롤 복원
    }
  };

  /**
   * 시간 관련 유틸리티
   */
  const TimeUtils = {
    /**
     * 상대적 시간 표시 (~초 전, ~분 전 등)
     * @param {string|Date} dateInput - ISO 형식의 날짜 문자열 또는 Date 객체
     * @returns {string} - 상대적 시간 문자열
     */
    formatRelativeTime: function (dateInput) {
      const date = dateInput instanceof Date ? dateInput : new Date(dateInput);
      const now = new Date();
      const seconds = Math.floor((now - date) / 1000);

      if (seconds < 30) {
        return "방금";
      }
      if (seconds < 60) {
        return `${seconds}초 전`;
      }

      const minutes = Math.floor(seconds / 60);
      if (minutes < 60) {
        return `${minutes}분 전`;
      }

      const hours = Math.floor(minutes / 60);
      if (hours < 24) {
        return `${hours}시간 전`;
      }

      const days = Math.floor(hours / 24);
      if (days < 7) {
        return `${days}일 전`;
      }

      const weeks = Math.floor(days / 7);
      if (weeks < 4) {
        return `${weeks}주일 전`;
      }

      const months = Math.floor(days / 30);
      return `${months}달 전`;
    },

    /**
     * 날짜 포맷 변환
     * @param {string|Date} dateInput - ISO 형식의 날짜 문자열 또는 Date 객체
     * @param {string} format - 출력 형식 ('short', 'medium', 'long')
     * @returns {string} - 포맷된 날짜 문자열
     */
    formatDate: function (dateInput, format = 'medium') {
      if (!dateInput) {
        return '';
      }

      const date = dateInput instanceof Date ? dateInput : new Date(dateInput);
      const options = {
        short: {year: 'numeric', month: '2-digit', day: '2-digit'},
        medium: {year: 'numeric', month: 'short', day: 'numeric'},
        long: {year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'}
      };

      return date.toLocaleDateString('ko-KR',
          options[format] || options.medium);
    }
  };

  /**
   * 네트워크 관련 유틸리티
   */
  const NetworkUtils = {
    /**
     * AJAX 요청 함수
     * @param {string} url - 요청 URL
     * @param {Object} options - 요청 옵션
     * @returns {Promise} - 응답 Promise 객체
     */
    fetchData: async function (url, options = {}) {
      const defaultOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'X-Requested-With': 'XMLHttpRequest'
        }
      };

      const mergedOptions = {...defaultOptions, ...options};

      // POST, PUT 등의 요청에 body가 있을 경우 JSON 문자열로 변환
      if (mergedOptions.body && typeof mergedOptions.body === 'object') {
        mergedOptions.body = JSON.stringify(mergedOptions.body);
      }

      try {
        const response = await fetch(url, mergedOptions);

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || '요청에 실패했습니다.');
        }

        // JSON 응답이 아닐 경우를 위한 처리
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
          return await response.json();
        }

        return await response.text();
      } catch (error) {
        console.error('Fetch error:', error);
        throw error;
      }
    }
  };

  /**
   * 문자열 관련 유틸리티
   */
  const StringUtils = {
    /**
     * 문자열 길이 제한 후 말줄임표 추가
     * @param {string} str - 원본 문자열
     * @param {number} maxLength - 최대 길이
     * @returns {string} - 처리된 문자열
     */
    truncate: function (str, maxLength) {
      if (!str) {
        return '';
      }
      if (str.length <= maxLength) {
        return str;
      }
      return str.substring(0, maxLength) + '...';
    },

    /**
     * HTML 이스케이프
     * @param {string} html - 이스케이프할 HTML 문자열
     * @returns {string} - 이스케이프된 문자열
     */
    escapeHtml: function (html) {
      const div = document.createElement('div');
      div.textContent = html;
      return div.innerHTML;
    }
  };

  /**
   * 브라우저 쿠키 관련 유틸리티
   */
  const CookieUtils = {
    /**
     * 쿠키 설정
     * @param {string} name - 쿠키 이름
     * @param {string} value - 쿠키 값
     * @param {number} days - 유효 기간(일)
     */
    set: function (name, value, days) {
      let expires = '';
      if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = '; expires=' + date.toUTCString();
      }
      document.cookie = name + '=' + encodeURIComponent(value) + expires
          + '; path=/';
    },

    /**
     * 쿠키 가져오기
     * @param {string} name - 쿠키 이름
     * @returns {string|null} - 쿠키 값 또는 null
     */
    get: function (name) {
      const nameEQ = name + '=';
      const ca = document.cookie.split(';');
      for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
          c = c.substring(1, c.length);
        }
        if (c.indexOf(nameEQ) === 0) {
          return decodeURIComponent(c.substring(nameEQ.length, c.length));
        }
      }
      return null;
    },

    /**
     * 쿠키 삭제
     * @param {string} name - 쿠키 이름
     */
    delete: function (name) {
      this.set(name, '', -1);
    }
  };

  /**
   * 검색 관련 유틸리티
   */
  const SearchUtils = {
    /**
     * 검색 핸들러 생성
     * 알파인 JS와 함께 사용
     * @returns {Object} - 검색 핸들러 객체
     */
    createSearchHandler: function () {
      return {
        searchQuery: '',
        suggestions: [],
        showSuggestions: false,
        selectedIndex: -1,
        searchTimeout: null,

        // 검색어 입력 시 추천 목록 업데이트
        updateSuggestions() {
          clearTimeout(this.searchTimeout);

          if (this.searchQuery.trim().length === 0) {
            this.suggestions = [];
            this.showSuggestions = false;
            return;
          }

          // 타이핑 멈춘 후 200ms 후에 검색 실행 (타이핑 중에는 API 호출 안함)
          this.searchTimeout = setTimeout(() => {
            this.fetchSuggestions();
          }, 200);
        },

        // API 호출하여 검색 추천 목록 가져오기
        async fetchSuggestions() {
          try {
            const response = await fetch(
                `/api/search/suggestions?query=${encodeURIComponent(
                    this.searchQuery)}`
            );

            if (response.ok) {
              const data = await response.json();
              this.suggestions = data;
              this.showSuggestions = data.length > 0;
              this.selectedIndex = -1;
            }
          } catch (error) {
            console.error('검색 추천 목록 가져오기 실패:', error);
            this.suggestions = [];
            this.showSuggestions = false;
          }
        },

        // 추천 목록에서 항목 선택
        selectSuggestion(suggestion) {
          // 인물 상세 페이지로 이동
          window.location.href = `/${suggestion.categoryId}/@${suggestion.name}`;
        },

        // 검색 폼 제출
        submitSearch(e) {
          if (this.selectedIndex >= 0 && this.suggestions[this.selectedIndex]) {
            e.preventDefault();
            this.selectSuggestion(this.suggestions[this.selectedIndex]);
          }
        },

        // 키보드 이벤트 처리 (화살표 키로 목록 이동)
        handleKeydown(e) {
          if (!this.showSuggestions) {
            return;
          }

          if (e.key === 'ArrowDown') {
            e.preventDefault();
            this.selectedIndex = Math.min(this.selectedIndex + 1,
                this.suggestions.length - 1);
          } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            this.selectedIndex = Math.max(this.selectedIndex - 1, -1);
          } else if (e.key === 'Enter' && this.selectedIndex >= 0) {
            e.preventDefault();
            this.selectSuggestion(this.suggestions[this.selectedIndex]);
          } else if (e.key === 'Escape') {
            this.showSuggestions = false;
          }
        }
      };
    }
  };

  /**
   * UI 관련 유틸리티
   */
  const UIUtils = {
    /**
     * 토스트 메시지 표시
     * @param {string} message - 표시할 메시지
     * @param {boolean} isError - 에러 메시지 여부
     * @param {number} duration - 표시 지속 시간(ms)
     */
    showToast: function (message, isError = false, duration = 3000) {
      // 기존 토스트 제거
      const existingToast = document.querySelector('.toast-message');
      if (existingToast) {
        existingToast.remove();
      }

      // 새 토스트 메시지 생성
      const toast = document.createElement('div');
      toast.className = `fixed bottom-4 right-4 py-2 px-4 rounded-lg shadow-lg z-50 toast-message transition-opacity duration-300 ${
          isError ? 'bg-red-600' : 'bg-indigo-600'
      } text-white`;
      toast.textContent = message;

      document.body.appendChild(toast);

      // 지정된 시간 후 제거
      setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => {
          toast.remove();
        }, 300);
      }, duration);
    },

    /**
     * 현재 페이지에 해당하는 네비게이션 링크 활성화
     */
    initNavActiveState: function () {
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
    },

    /**
     * 스크롤 상단으로 버튼 설정
     */
    setupScrollToTopButton: function () {
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
    },

    /**
     * 드롭다운 메뉴 설정
     */
    setupDropdowns: function () {
      const dropdownButtons = document.querySelectorAll(
          '[data-dropdown-toggle]');

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
  };

  // 공개 API 정의
  return {
    init: function () {
      // 페이지 로드 시 자동 초기화 작업
      document.addEventListener('DOMContentLoaded', function () {
        ImageUtils.setupErrorHandling();
        UIUtils.initNavActiveState();
        UIUtils.setupScrollToTopButton();
        UIUtils.setupDropdowns();
      });
    },
    image: ImageUtils,
    dom: DOMUtils,
    time: TimeUtils,
    network: NetworkUtils,
    string: StringUtils,
    cookie: CookieUtils,
    search: SearchUtils,
    ui: UIUtils
  };
})();

// 자동 초기화
CommonUtils.init();

// 전역 객체로 노출
window.CommonUtils = CommonUtils;

// ToastManager 호환성 레이어 (기존 코드와의 호환성 유지)
window.ToastManager = {
  show: function (message, isError = false) {
    CommonUtils.ui.showToast(message, isError);
  }
};