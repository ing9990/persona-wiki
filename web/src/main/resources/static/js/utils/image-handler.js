/**
 * 이미지 처리를 위한 자바스크립트 파일
 * 작성일: 2025-03-30
 *
 * 이미지 미리보기, 이미지 로드 오류 처리 등의 기능을 담당합니다.
 */

class ImageHandler {
  /**
   * 이미지 핸들러 초기화
   * @param {Object} options - 설정 옵션
   */
  constructor(options) {
    this.options = {
      defaultPlaceholderUrl: '/img/profile-placeholder.png',
      categoryPlaceholderUrl: '/img/category-placeholder.jpg',
      lazyLoadSelector: 'img[data-lazy="true"]',
      errorHandlingSelector: 'img[data-handle-error="true"]',
      ...options
    };

    this.init();
  }

  /**
   * 핸들러 초기화
   */
  init() {
    // 이미지 오류 처리
    this.setupErrorHandling();

    // 지연 로딩 설정
    this.setupLazyLoading();

    // Intersection Observer API 지원 확인
    if ('IntersectionObserver' in window) {
      this.setupIntersectionObserver();
    }
  }

  /**
   * 이미지 오류 처리 설정
   */
  setupErrorHandling() {
    // 카테고리 이미지 오류 처리
    document.querySelectorAll(
        '.category-image, .category-header-image').forEach(img => {
      img.onerror = () => this.handleImageError(img,
          this.options.categoryPlaceholderUrl);
    });

    // 인물 이미지 오류 처리
    document.querySelectorAll(
        '.figure-image, .profile-header-image, .profile-image').forEach(img => {
      img.onerror = () => this.handleImageError(img,
          this.options.defaultPlaceholderUrl);
    });

    // 데이터 속성으로 지정된 이미지 오류 처리
    document.querySelectorAll(this.options.errorHandlingSelector).forEach(
        img => {
          const placeholderUrl = img.getAttribute('data-placeholder')
              || this.options.defaultPlaceholderUrl;
          img.onerror = () => this.handleImageError(img, placeholderUrl);
        });
  }

  /**
   * 이미지 오류 처리
   * @param {HTMLImageElement} img - 이미지 요소
   * @param {string} fallbackSrc - 대체 이미지 경로
   */
  handleImageError(img, fallbackSrc) {
    img.onerror = null; // 무한 루프 방지
    img.src = fallbackSrc;
  }

  /**
   * 지연 로딩 설정
   */
  setupLazyLoading() {
    document.querySelectorAll(this.options.lazyLoadSelector).forEach(img => {
      // 아직 로드되지 않은 이미지에만 적용
      if (img.getAttribute('data-loaded') !== 'true') {
        // 원본 이미지 URL을 data-src 속성에 저장
        const src = img.getAttribute('src');
        if (src) {
          img.setAttribute('data-src', src);
          // 처음에는 플레이스홀더 이미지로 설정
          img.src = img.getAttribute('data-placeholder')
              || this.options.defaultPlaceholderUrl;
        }
      }
    });
  }

  /**
   * Intersection Observer 설정
   */
  setupIntersectionObserver() {
    const lazyImageObserver = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const img = entry.target;
          // 원본 이미지 로드
          const dataSrc = img.getAttribute('data-src');
          if (dataSrc) {
            img.src = dataSrc;
            img.setAttribute('data-loaded', 'true');
            // 성공적으로 로드되면 옵저버에서 제거
            lazyImageObserver.unobserve(img);
          }
        }
      });
    }, {
      rootMargin: '0px 0px 200px 0px', // 화면 아래 200px 이전에 로드 시작
      threshold: 0.01 // 1% 이상 보일 때 로드
    });

    // 지연 로딩 대상 이미지에 옵저버 적용
    document.querySelectorAll(this.options.lazyLoadSelector).forEach(img => {
      lazyImageObserver.observe(img);
    });
  }

  /**
   * 이미지 미리보기 설정
   * @param {string} imageUrlInputId - 이미지 URL 입력 필드 ID
   * @param {string} previewImgId - 미리보기 이미지 요소 ID
   * @param {string} previewPlaceholderId - 미리보기 플레이스홀더 ID
   */
  setupImagePreview(imageUrlInputId, previewImgId, previewPlaceholderId) {
    const imageUrlInput = document.getElementById(imageUrlInputId);
    const previewImg = document.getElementById(previewImgId);
    const previewPlaceholder = document.getElementById(previewPlaceholderId);

    if (!imageUrlInput || !previewImg || !previewPlaceholder) {
      console.error('이미지 미리보기 설정에 필요한 요소를 찾을 수 없습니다.');
      return;
    }

    // 이미지 URL 입력 시 미리보기 업데이트
    imageUrlInput.addEventListener('input', () => {
      this.updateImagePreview(imageUrlInput, previewImg, previewPlaceholder);
    });

    // 초기 로드 시 미리보기 업데이트
    this.updateImagePreview(imageUrlInput, previewImg, previewPlaceholder);
  }

  /**
   * 이미지 미리보기 업데이트
   * @param {HTMLInputElement} imageUrlInput - 이미지 URL 입력 필드
   * @param {HTMLImageElement} previewImg - 미리보기 이미지 요소
   * @param {HTMLElement} previewPlaceholder - 미리보기 플레이스홀더
   */
  updateImagePreview(imageUrlInput, previewImg, previewPlaceholder) {
    const imageUrl = imageUrlInput.value.trim();

    if (imageUrl) {
      previewImg.src = imageUrl;
      previewImg.classList.remove('hidden');
      previewPlaceholder.classList.add('hidden');

      // 이미지 로드 오류 처리
      previewImg.onerror = () => {
        previewImg.classList.add('hidden');
        previewPlaceholder.classList.remove('hidden');
      };
    } else {
      previewImg.classList.add('hidden');
      previewPlaceholder.classList.remove('hidden');
    }
  }

  /**
   * 전체 이미지 리프레시
   * 동적으로 추가된 이미지 요소에 오류 처리 적용
   */
  refreshImages() {
    // 오류 처리 재설정
    this.setupErrorHandling();

    // 새로운 지연 로딩 이미지 설정
    this.setupLazyLoading();

    // Intersection Observer 지원 시 새 이미지에 적용
    if ('IntersectionObserver' in window) {
      this.setupIntersectionObserver();
    }
  }
}

// 문서 로드 완료 시 이미지 핸들러 인스턴스 생성
document.addEventListener('DOMContentLoaded', function () {
  window.imageHandler = new ImageHandler();
});

// 다른 스크립트에서 사용할 수 있도록 내보내기
window.ImageHandler = ImageHandler;