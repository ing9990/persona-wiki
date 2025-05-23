/**
 * 인물 폼 처리를 위한 자바스크립트 파일
 * 작성일: 2025-03-30
 *
 * 인물 추가 및 수정 폼의 제출 처리, 이미지 미리보기 등의 기능을 담당합니다.
 */

class FigureFormHandler {
  /**
   * 인물 폼 핸들러 초기화
   * @param {Object} options - 설정 옵션
   * @param {string} options.formId - 폼 ID
   * @param {string} options.imageUrlId - 이미지 URL 입력 필드 ID
   * @param {string} options.previewImgId - 이미지 미리보기 요소 ID
   * @param {string} options.previewPlaceholderId - 미리보기 플레이스홀더 ID
   * @param {string} options.successAlertId - 성공 알림 요소 ID
   * @param {string} options.errorAlertId - 오류 알림 요소 ID
   * @param {string} options.errorMessageId - 오류 메시지 요소 ID
   * @param {string} options.mode - 'add' 또는 'edit' 모드
   */
  constructor(options) {
    this.options = {
      formId: 'figureForm',
      imageUrlId: 'imageUrl',
      previewImgId: 'previewImg',
      previewPlaceholderId: 'previewPlaceholder',
      successAlertId: 'successAlert',
      errorAlertId: 'errorAlert',
      errorMessageId: 'errorMessage',
      mode: 'add',
      ...options
    };

    this.form = document.getElementById(this.options.formId);
    this.imageUrlInput = document.getElementById(this.options.imageUrlId);
    this.previewImg = document.getElementById(this.options.previewImgId);
    this.previewPlaceholder = document.getElementById(
        this.options.previewPlaceholderId);
    this.successAlert = document.getElementById(this.options.successAlertId);
    this.errorAlert = document.getElementById(this.options.errorAlertId);
    this.errorMessage = document.getElementById(this.options.errorMessageId);

    this.init();
  }

  /**
   * 핸들러 초기화
   */
  init() {
    if (!this.form) {
      console.error('Form element not found');
      return;
    }

    // 이미지 URL 입력 시 미리보기 업데이트
    if (this.imageUrlInput) {
      this.imageUrlInput.addEventListener('input',
          this.updateImagePreview.bind(this));
      // 초기 로드 시 미리보기 업데이트
      this.updateImagePreview();
    }

    // 폼 제출 이벤트 리스너 등록
    this.form.addEventListener('submit', this.handleSubmit.bind(this));
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
   * 폼 데이터 수집
   * @returns {Object} 인물 데이터 객체
   */
  collectFormData() {
    const name = document.getElementById('name').value.trim();
    const categoryId = document.getElementById('categoryId').value;
    const imageUrl = this.imageUrlInput.value.trim();
    const bio = document.getElementById('bio').value.trim();

    return {
      name,
      categoryId,
      imageUrl: imageUrl || null,
      bio: bio || null
    };
  }

  /**
   * 폼 유효성 검사
   * @param {Object} data - 폼 데이터
   * @returns {boolean} 유효성 검사 통과 여부
   */
  validateFormData(data) {
    if (!data.name) {
      this.showError('인물 이름을 입력해주세요.');
      return false;
    }

    if (!data.categoryId) {
      this.showError('카테고리를 선택해주세요.');
      return false;
    }

    return true;
  }

  /**
   * 폼 제출 처리
   * @param {Event} e - 제출 이벤트
   */
  async handleSubmit(e) {
    e.preventDefault();

    // 폼 데이터 수집 및 유효성 검사
    const figureData = this.collectFormData();
    if (!this.validateFormData(figureData)) {
      return;
    }

    try {
      // 폼 제출 버튼 비활성화
      const submitButton = this.form.querySelector('button[type="submit"]');
      if (submitButton) {
        submitButton.disabled = true;
        submitButton.classList.add('opacity-50');
      }

      let response;
      let url = '/api/figures';
      let method = 'POST';

      // 수정 모드인 경우 다른 URL과 메서드 사용
      if (this.options.mode === 'edit') {
        const figureId = document.getElementById('figureId').value;
        url = `/api/figures/${figureId}`;
        method = 'PUT';
      }

      // API 호출
      response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(figureData)
      });

      // 응답 처리
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || '인물 정보 처리에 실패했습니다.');
      }

      const data = await response.json();

      // 성공 알림 표시
      this.showSuccess();

      // 폼 초기화
      this.form.reset();
      this.updateImagePreview();

      // 3초 후 인물 상세 페이지로 이동
      setTimeout(() => {
        window.location.href = `/${data.categoryId}/@${data.name}`;
      }, 1500);
    } catch (error) {
      console.error('Error:', error);
      this.showError(error.message);
    } finally {
      // 폼 제출 버튼 다시 활성화
      const submitButton = this.form.querySelector('button[type="submit"]');
      if (submitButton) {
        submitButton.disabled = false;
        submitButton.classList.remove('opacity-50');
      }
    }
  }

  /**
   * 성공 알림 표시
   */
  showSuccess() {
    if (this.successAlert) {
      this.successAlert.classList.remove('hidden');
    }
    if (this.errorAlert) {
      this.errorAlert.classList.add('hidden');
    }
  }

  /**
   * 오류 알림 표시
   * @param {string} message - 오류 메시지
   */
  showError(message) {
    if (this.errorAlert) {
      this.errorAlert.classList.remove('hidden');
    }
    if (this.errorMessage) {
      this.errorMessage.textContent = message;
    }
    if (this.successAlert) {
      this.successAlert.classList.add('hidden');
    }
  }
}

// 문서 로드 완료 시 인물 폼 핸들러 인스턴스 생성
document.addEventListener('DOMContentLoaded', function () {
  // URL 경로에 따라 다른 모드 설정
  const path = window.location.pathname;
  const mode = path.includes('/edit-figure/') ? 'edit' : 'add';

  // 인물 폼 핸들러 초기화
  new FigureFormHandler({mode});
});

// 다른 스크립트에서 사용할 수 있도록 내보내기
window.FigureFormHandler = FigureFormHandler;