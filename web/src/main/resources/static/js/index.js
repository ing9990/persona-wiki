/**
 * 국민사형투표 공통 JavaScript 파일
 * 작성일: 2025-03-29
 */

// DOMContentLoaded 이벤트
document.addEventListener('DOMContentLoaded', function () {

  // 공통 초기화 함수 실행
  initTooltips();
  initDropdowns();
  initToasts();
  initNavActiveState();
  initSearchBar();
});

/**
 * Bootstrap 툴팁 초기화
 */
function initTooltips() {
  const tooltipTriggerList = document.querySelectorAll(
      '[data-bs-toggle="tooltip"]');
  [...tooltipTriggerList].map(
      tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
}

/**
 * Bootstrap 드롭다운 초기화
 */
function initDropdowns() {
  const dropdownElementList = document.querySelectorAll('.dropdown-toggle');
  [...dropdownElementList].map(
      dropdownToggleEl => new bootstrap.Dropdown(dropdownToggleEl));
}

/**
 * Bootstrap 토스트 메시지 초기화
 */
function initToasts() {
  const toastElList = document.querySelectorAll('.toast');
  [...toastElList].map(toastEl => new bootstrap.Toast(toastEl));
}

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
 * 검색창 초기화 및 유효성 검사
 */
function initSearchBar() {
  const searchForm = document.querySelector('form[action="/search"]');

  if (searchForm) {
    searchForm.addEventListener('submit', function (event) {
      const searchInput = this.querySelector('input[name="query"]');
      const query = searchInput.value.trim();

      // 빈 검색어 제출 방지
      if (!query) {
        event.preventDefault();
        searchInput.focus();
      }
    });
  }
}

/**
 * 페이지 상단으로 스크롤
 */
function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
}

/**
 * 문자열 길이 제한 후 말줄임표 추가
 * @param {string} str - 원본 문자열
 * @param {number} maxLength - 최대 길이
 * @returns {string} - 처리된 문자열
 */
function truncateString(str, maxLength) {
  if (!str) {
    return '';
  }
  if (str.length <= maxLength) {
    return str;
  }

  return str.substring(0, maxLength) + '...';
}

/**
 * 날짜 포맷 변환
 * @param {string} dateString - ISO 형식의 날짜 문자열
 * @param {string} format - 출력 형식 ('short', 'medium', 'long')
 * @returns {string} - 포맷된 날짜 문자열
 */
function formatDate(dateString, format = 'medium') {
  if (!dateString) {
    return '';
  }

  const date = new Date(dateString);

  const options = {
    short: {year: 'numeric', month: '2-digit', day: '2-digit'},
    medium: {year: 'numeric', month: 'short', day: 'numeric'},
    long: {year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'}
  };

  return date.toLocaleDateString('ko-KR', options[format] || options.medium);
}

/**
 * 이미지 로딩 오류 처리
 * @param {HTMLImageElement} img - 이미지 요소
 * @param {string} fallbackSrc - 대체 이미지 경로
 */
function handleImageError(img, fallbackSrc = '/img/placeholder.jpg') {
  img.onerror = null; // 무한 루프 방지
  img.src = fallbackSrc;
}

/**
 * 브라우저 쿠키 설정
 * @param {string} name - 쿠키 이름
 * @param {string} value - 쿠키 값
 * @param {number} days - 유효 기간(일)
 */
function setCookie(name, value, days) {
  let expires = '';

  if (days) {
    const date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    expires = '; expires=' + date.toUTCString();
  }

  document.cookie = name + '=' + encodeURIComponent(value) + expires
      + '; path=/';
}

/**
 * 브라우저 쿠키 가져오기
 * @param {string} name - 쿠키 이름
 * @returns {string|null} - 쿠키 값 또는 null
 */
function getCookie(name) {
  const nameEQ = name + '=';
  const ca = document.cookie.split(';');

  for (let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) === ' ') {
      c = c.substring(1, c.length);
    }
    if (c.indexOf(nameEQ) === 0) {
      return decodeURIComponent(
          c.substring(nameEQ.length, c.length));
    }
  }

  return null;
}

/**
 * 브라우저 쿠키 삭제
 * @param {string} name - 쿠키 이름
 */
function deleteCookie(name) {
  setCookie(name, '', -1);
}

/**
 * AJAX 요청 함수
 * @param {string} url - 요청 URL
 * @param {string} method - HTTP 메서드 (GET, POST 등)
 * @param {Object} data - 요청 데이터
 * @returns {Promise} - 응답 Promise 객체
 */
function ajaxRequest(url, method = 'GET', data = null) {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open(method, url);

    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');

    xhr.onload = function () {
      if (xhr.status >= 200 && xhr.status < 300) {
        try {
          const response = JSON.parse(xhr.responseText);
          resolve(response);
        } catch (e) {
          resolve(xhr.responseText);
        }
      } else {
        reject({
          status: xhr.status,
          statusText: xhr.statusText
        });
      }
    };

    xhr.onerror = function () {
      reject({
        status: xhr.status,
        statusText: xhr.statusText
      });
    };

    if (data) {
      xhr.send(JSON.stringify(data));
    } else {
      xhr.send();
    }
  });
}