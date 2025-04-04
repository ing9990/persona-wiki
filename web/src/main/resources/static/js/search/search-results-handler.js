/**
 * 검색 결과 페이지 핸들러
 * 검색어 하이라이트, 이미지 에러 처리 등을 담당합니다.
 */
class SearchResultsHandler {
  constructor() {
    this.handleSearchHighlight();
    this.handleImageErrors();
  }

  /**
   * 검색어 하이라이트 기능
   */
  handleSearchHighlight() {
    // Thymeleaf에서 전달된 검색어 가져오기
    const query = document.querySelector(
        'span.text-indigo-600').textContent.trim();

    if (query && query.length > 1) {
      const regex = new RegExp(query, 'gi');

      // 이름 하이라이트
      document.querySelectorAll('.search-result-card h3').forEach(element => {
        const text = element.textContent;
        element.innerHTML = text.replace(regex,
            match => `<span class="highlight">${match}</span>`);
      });

      // 소개글 하이라이트
      document.querySelectorAll('.search-result-card p').forEach(element => {
        const text = element.textContent;
        element.innerHTML = text.replace(regex,
            match => `<span class="highlight">${match}</span>`);
      });
    }
  }

  /**
   * 이미지 로딩 오류 처리
   */
  handleImageErrors() {
    const images = document.querySelectorAll('img[data-handle-error="true"]');

    images.forEach(img => {
      img.addEventListener('error', () => {
        const placeholderSrc = img.getAttribute('data-placeholder')
            || '/img/profile-placeholder.svg';
        img.src = placeholderSrc;
      });
    });
  }
}

// 페이지 로드 시 핸들러 초기화
document.addEventListener('DOMContentLoaded', () => {
  new SearchResultsHandler();
});