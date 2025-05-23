/**
 * 공통 유틸리티 함수 모음
 */
class Utils {
  /**
   * 문자열 길이 제한 후 말줄임표 추가
   * @param {string} str - 원본 문자열
   * @param {number} maxLength - 최대 길이
   * @returns {string} - 처리된 문자열
   */
  static truncateString(str, maxLength) {
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
  static formatDate(dateString, format = 'medium') {
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
  static handleImageError(img, fallbackSrc = '/img/placeholder.jpg') {
    img.onerror = null; // 무한 루프 방지
    img.src = fallbackSrc;
  }

  /**
   * AJAX 요청 함수
   * @param {string} url - 요청 URL
   * @param {Object} options - 요청 옵션
   * @returns {Promise} - 응답 Promise 객체
   */
  static async fetchData(url, options = {}) {
    const defaultOptions = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      }
    };

    const mergedOptions = {...defaultOptions, ...options};

    try {
      const response = await fetch(url, mergedOptions);

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || '요청에 실패했습니다.');
      }

      return await response.json();
    } catch (error) {
      console.error('Fetch error:', error);
      throw error;
    }
  }
}

// 전역 범위에서 접근 가능하도록 노출
window.Utils = Utils;