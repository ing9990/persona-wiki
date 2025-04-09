/**
 * 댓글 관련 유틸리티 함수 모음
 * 재사용 가능한 코드들을 모아둔 유틸리티 클래스입니다.
 */
class CommentUtils {
  /**
   * 댓글 목록 요소에서 figure-id를 가져옵니다.
   * @returns {string} 피규어 ID
   */
  static getFigureId() {
    const commentList = document.getElementById('comment-list');
    if (commentList && commentList.dataset.figureId) {
      return commentList.dataset.figureId;
    }

    // 대체 방법으로 meta 태그 확인
    const figureIdMeta = document.querySelector('meta[name="figure-id"]');
    if (figureIdMeta && figureIdMeta.content) {
      return figureIdMeta.content;
    }

    // 프로필 헤더에서 확인
    const profileHeader = document.querySelector('.profile-header');
    if (profileHeader && profileHeader.dataset.figureId) {
      return profileHeader.dataset.figureId;
    }

    console.error('figure-id를 찾을 수 없습니다');
    return '';
  }

  /**
   * 요소를 표시합니다.
   * @param {HTMLElement} element - 표시할 요소
   */
  static showElement(element) {
    if (element && element.classList.contains('hidden')) {
      element.classList.remove('hidden');
    }
  }

  /**
   * 요소를 숨깁니다.
   * @param {HTMLElement} element - 숨길 요소
   */
  static hideElement(element) {
    if (element && !element.classList.contains('hidden')) {
      element.classList.add('hidden');
    }
  }

  /**
   * 요소 표시 상태를 토글합니다.
   * @param {HTMLElement} element - 토글할 요소
   */
  static toggleElement(element) {
    if (element) {
      element.classList.toggle('hidden');
    }
  }

  /**
   * 로그인 상태를 확인합니다.
   * @returns {boolean} 로그인 여부
   */
  static isLoggedIn() {
    const userLoginStatus = document.getElementById('userLoginStatus');
    return userLoginStatus && userLoginStatus.value === 'true';
  }

  /**
   * 현재 날짜를 ISO 문자열로 반환합니다.
   * @returns {string} ISO 형식 날짜 문자열
   */
  static getCurrentISODate() {
    return new Date().toISOString();
  }

  /**
   * 이미지 오류 처리 함수
   * @param {HTMLImageElement} img - 이미지 요소
   * @param {string} fallbackSrc - 대체 이미지 경로
   */
  static handleImageError(img, fallbackSrc = '/img/profile-placeholder.svg') {
    img.onerror = null; // 무한 루프 방지
    img.src = fallbackSrc;
  }
}

// 전역으로 노출
window.CommentUtils = CommentUtils;