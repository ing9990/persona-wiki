/**
 * 카테고리 목록 페이지 핸들러
 * 이미지 에러 처리를 담당합니다.
 */
class CategoryListHandler {
  constructor() {
    this.handleImageErrors();
  }

  /**
   * 이미지 로딩 오류 처리
   */
  handleImageErrors() {
    const images = document.querySelectorAll('img[data-handle-error="true"]');

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
  new CategoryListHandler();
});