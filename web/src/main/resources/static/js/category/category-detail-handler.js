/**
 * 카테고리 상세 페이지 핸들러
 * 정렬 기능, 이미지 오류 처리 등을 담당합니다.
 */
class CategoryDetailHandler {
  constructor() {
    this.initializeEventListeners();
    this.handleImageErrors();
  }

  /**
   * 이벤트 리스너 초기화
   */
  initializeEventListeners() {
    const sortSelect = document.getElementById('sortOption');
    if (sortSelect) {
      sortSelect.addEventListener('change', this.handleSortChange.bind(this));
    }
  }

  /**
   * 인물 카드 정렬 기능
   * @param {Event} event - 정렬 선택 이벤트
   */
  handleSortChange(event) {
    const sortBy = event.target.value;
    const container = document.querySelector('.grid');

    if (!container) {
      return;
    }

    const items = Array.from(container.children);

    const sortedItems = items.sort((a, b) => {
      switch (sortBy) {
        case 'name':
          return this.sortByName(a, b);
        case 'newest':
          return this.sortByNewest(a, b);
        case 'popular':
          return this.sortByPopularity(a, b);
        default:
          return 0;
      }
    });

    // 정렬된 순서대로 DOM에 다시 삽입
    sortedItems.forEach(item => {
      container.appendChild(item);
    });
  }

  /**
   * 이름순 정렬
   * @param {HTMLElement} a - 첫 번째 요소
   * @param {HTMLElement} b - 두 번째 요소
   * @returns {number} 정렬 순서
   */
  sortByName(a, b) {
    const nameA = a.querySelector('h3').textContent.trim();
    const nameB = b.querySelector('h3').textContent.trim();
    return nameA.localeCompare(nameB);
  }

  /**
   * 최신순 정렬 (임시 구현 - 실제 구현은 날짜 데이터 필요)
   * @param {HTMLElement} a - 첫 번째 요소
   * @param {HTMLElement} b - 두 번째 요소
   * @returns {number} 정렬 순서
   */
  sortByNewest(a, b) {
    // 실제 구현을 위해서는 날짜 데이터가 필요
    return 0;
  }

  /**
   * 인기도순 정렬
   * @param {HTMLElement} a - 첫 번째 요소
   * @param {HTMLElement} b - 두 번째 요소
   * @returns {number} 정렬 순서
   */
  sortByPopularity(a, b) {
    const likesA = parseInt(
        a.querySelector('.fa-crown').nextElementSibling.textContent);
    const likesB = parseInt(
        b.querySelector('.fa-crown').nextElementSibling.textContent);
    return likesB - likesA; // 내림차순
  }

  /**
   * 이미지 로딩 오류 처리
   */
  handleImageErrors() {
    const images = document.querySelectorAll(
        '.category-header-image, img[data-handle-error="true"]');

    images.forEach(img => {
      img.addEventListener('error', () => {
        const placeholderSrc = img.getAttribute('data-placeholder')
            || '/img/category-placeholder.jpg';
        img.src = placeholderSrc;
      });
    });
  }
}

// 페이지 로드 시 핸들러 초기화
document.addEventListener('DOMContentLoaded', () => {
  new CategoryDetailHandler();
});