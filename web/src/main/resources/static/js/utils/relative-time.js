/**
 * 상대적 시간 표시를 위한 유틸리티
 * 날짜를 '방금 전', 'N분 전' 등의 형식으로 변환
 */
var RelativeTimeUtils = (function () {

  /**
   * 주어진 날짜 문자열을 상대적 시간으로 변환
   * @param {string} dateStr - ISO 형식 또는 일반 날짜 형식 문자열
   * @returns {string} 상대적 시간 문자열
   */
  function formatRelativeTime(dateStr) {
    if (!dateStr) {
      return '';
    }

    // 날짜 파싱 (ISO 형식 또는 YYYY-MM-DD HH:MM:SS 형식 모두 처리)
    let date;
    if (dateStr.includes('T')) {
      // ISO 형식 (2025-04-08T16:00:52)
      date = new Date(dateStr);
    } else {
      // 일반 형식 (2025-04-08 16:00:52)
      const parts = dateStr.split(/[- :]/);
      if (parts.length >= 6) {
        date = new Date(parts[0], parts[1] - 1, parts[2], parts[3], parts[4],
            parts[5]);
      } else {
        date = new Date(dateStr);
      }
    }

    // 유효하지 않은 날짜인 경우 원본 문자열 반환
    if (isNaN(date.getTime())) {
      return dateStr;
    }

    const now = new Date();
    const diffInSeconds = Math.floor((now - date) / 1000);

    // 1분 미만
    if (diffInSeconds < 60) {
      return '방금 전';
    }

    // 1시간 미만
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) {
      return `${diffInMinutes}분 전`;
    }

    // 24시간 미만
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) {
      return `${diffInHours}시간 전`;
    }

    // 30일 미만
    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays < 30) {
      return `${diffInDays}일 전`;
    }

    // 12개월 미만
    const diffInMonths = Math.floor(diffInDays / 30);
    if (diffInMonths < 12) {
      return `${diffInMonths}개월 전`;
    }

    // 1년 이상
    const diffInYears = Math.floor(diffInMonths / 12);
    return `${diffInYears}년 전`;
  }

  /**
   * 페이지 내의 모든 날짜 요소를 상대적 시간으로 변환
   * @param {string} selector - 날짜 요소의 CSS 선택자
   */
  function updateAllRelativeTimes(selector) {
    const dateElements = document.querySelectorAll(selector);
    dateElements.forEach(element => {
      const originalDate = element.getAttribute('data-original-date')
          || element.textContent;
      element.setAttribute('data-original-date', originalDate);
      element.setAttribute('title', originalDate); // 툴팁으로 원본 날짜 표시
      element.textContent = formatRelativeTime(originalDate);
    });
  }

  /**
   * 새로 추가된 날짜 요소를 상대적 시간으로 변환
   * @param {HTMLElement} element - 날짜 요소
   * @param {string} dateStr - 날짜 문자열
   */
  function setRelativeTime(element, dateStr) {
    if (!element || !dateStr) {
      return;
    }

    element.setAttribute('data-original-date', dateStr);
    element.setAttribute('title', dateStr); // 툴팁으로 원본 날짜 표시
    element.textContent = formatRelativeTime(dateStr);
  }

  /**
   * 주기적으로 상대적 시간 업데이트
   * @param {string} selector - 날짜 요소의 CSS 선택자
   * @param {number} intervalMs - 업데이트 주기 (밀리초)
   */
  function startPeriodicUpdates(selector, intervalMs = 60000) {
    updateAllRelativeTimes(selector);

    // 1분마다 업데이트
    setInterval(() => {
      updateAllRelativeTimes(selector);
    }, intervalMs);
  }

  // 공개 API
  return {
    formatRelativeTime: formatRelativeTime,
    updateAllRelativeTimes: updateAllRelativeTimes,
    setRelativeTime: setRelativeTime,
    startPeriodicUpdates: startPeriodicUpdates
  };
})();

// 페이지 로드 시 상대적 시간 적용
document.addEventListener('DOMContentLoaded', function () {
  // 페이지에 있는 모든 댓글 날짜에 대해 상대적 시간 적용
  RelativeTimeUtils.startPeriodicUpdates(
      '.comment-date, .comment-date-relative');
});