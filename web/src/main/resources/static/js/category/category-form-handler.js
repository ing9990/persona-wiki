/**
 * 카테고리 추가 페이지 핸들러
 * 카테고리 추가 폼 처리, 이미지 미리보기, 유효성 검사 등을 담당합니다.
 */
class CategoryFormHandler {
  constructor() {
    this.initializeElements();
    this.addEventListeners();
  }

  /**
   * DOM 요소 초기화
   */
  initializeElements() {
    this.categoryForm = document.getElementById('categoryForm');
    this.imageUrlInput = document.getElementById('imageUrl');
    this.previewImg = document.getElementById('previewImg');
    this.previewPlaceholder = document.getElementById('previewPlaceholder');
    this.idInput = document.getElementById('id');
    this.successAlert = document.getElementById('successAlert');
    this.errorAlert = document.getElementById('errorAlert');
    this.errorMessage = document.getElementById('errorMessage');
  }

  /**
   * 이벤트 리스너 추가
   */
  addEventListeners() {
    // 카테고리 ID 입력 필드 이벤트 (영문 소문자, 숫자, 하이픈만 허용)
    this.idInput.addEventListener('input', () => {
      this.idInput.value = this.idInput.value
      .toLowerCase()
      .replace(/[^a-z0-9-]/g, '');
    });

    // 이미지 URL 변경 시 미리보기 업데이트
    this.imageUrlInput.addEventListener('input',
        this.updateImagePreview.bind(this));

    // 폼 제출 이벤트
    this.categoryForm.addEventListener('submit', this.handleSubmit.bind(this));
  }

  /**
   * 이미지 미리보기 업데이트
   */
  updateImagePreview() {
    const imageUrl = this.imageUrlInput.value.trim();

    if (imageUrl) {
      this.previewImg.src = imageUrl;
      this.previewImg.classList.remove('hidden');
      this.previewPlaceholder.classList.add('hidden');

      // 이미지 로드 오류 처리
      this.previewImg.onerror = () => {
        this.previewImg.classList.add('hidden');
        this.previewPlaceholder.classList.remove('hidden');
      };
    } else {
      this.previewImg.classList.add('hidden');
      this.previewPlaceholder.classList.remove('hidden');
    }
  }

  /**
   * 폼 제출 처리
   * @param {Event} e - 폼 제출 이벤트
   */
  handleSubmit(e) {
    e.preventDefault();

    // 필수 필드 값 가져오기
    const id = this.categoryForm.querySelector('#id').value.trim();
    const displayName = this.categoryForm.querySelector(
        '#displayName').value.trim();
    const imageUrl = this.categoryForm.querySelector('#imageUrl').value.trim()
        || null;
    const description = this.categoryForm.querySelector(
        '#description').value.trim() || null;

    // 유효성 검사
    if (!id || !displayName) {
      this.showErrorMessage('필수 항목을 모두 입력해주세요.');
      return;
    }

    // 카테고리 데이터 객체 생성
    const categoryData = {
      id,
      displayName,
      imageUrl,
      description
    };

    // API 호출
    this.submitCategory(categoryData);
  }

  /**
   * 카테고리 추가 API 호출
   * @param {Object} categoryData - 카테고리 정보
   */
  submitCategory(categoryData) {
    fetch('/api/categories', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(categoryData)
    })
    .then(response => {
      if (!response.ok) {
        return response.text().then(text => {
          throw new Error(text || '카테고리 추가에 실패했습니다.');
        });
      }
      return response.json();
    })
    .then(data => {
      // 성공 알림 표시
      this.showSuccessMessage();

      // 폼 초기화
      this.categoryForm.reset();
      this.previewImg.classList.add('hidden');
      this.previewPlaceholder.classList.remove('hidden');

      // 3초 후 카테고리 목록 페이지로 이동
      setTimeout(() => {
        window.location.href = '/categories';
      }, 1500);
    })
    .catch(error => {
      // 에러 알림 표시
      this.showErrorMessage(error.message);
      console.error('Error:', error);
    });
  }

  /**
   * 성공 메시지 표시
   */
  showSuccessMessage() {
    this.successAlert.classList.remove('hidden');
    this.errorAlert.classList.add('hidden');
  }

  /**
   * 에러 메시지 표시
   * @param {string} message - 에러 메시지
   */
  showErrorMessage(message) {
    this.errorMessage.textContent = message;
    this.errorAlert.classList.remove('hidden');
    this.successAlert.classList.add('hidden');
  }
}

// 페이지 로드 시 핸들러 초기화
document.addEventListener('DOMContentLoaded', () => {
  new CategoryFormHandler();
});