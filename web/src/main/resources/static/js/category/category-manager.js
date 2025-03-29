/**
 * 카테고리 관리를 위한 자바스크립트 파일
 * 작성일: 2025-03-30
 *
 * 카테고리 추가, 수정, 모달 관리 등의 기능을 담당합니다.
 */

class CategoryManager {
  /**
   * 카테고리 매니저 초기화
   * @param {Object} options - 설정 옵션
   * @param {string} options.modalId - 모달 ID
   * @param {string} options.formId - 폼 ID
   * @param {string} options.addButtonId - 추가 버튼 ID
   * @param {string} options.closeButtonId - 닫기 버튼 ID
   * @param {string} options.cancelButtonId - 취소 버튼 ID
   * @param {string} options.saveButtonId - 저장 버튼 ID
   * @param {string} options.categoryIdInputId - 카테고리 ID 입력 필드 ID
   * @param {string} options.categorySelectId - 카테고리 선택 드롭다운 ID
   */
  constructor(options) {
    this.options = {
      modalId: 'categoryModal',
      formId: 'categoryForm',
      addButtonId: 'addCategoryBtn',
      closeButtonId: 'closeModalBtn',
      cancelButtonId: 'cancelModalBtn',
      saveButtonId: 'saveCategoryBtn',
      categoryIdInputId: 'categoryIdModal',
      displayNameInputId: 'displayNameModal',
      descriptionInputId: 'descriptionModal',
      categoryImageUrlInputId: 'categoryImageUrlModal',
      categorySelectId: 'categoryId',
      ...options
    };

    this.modal = document.getElementById(this.options.modalId);
    this.form = document.getElementById(this.options.formId);
    this.addButton = document.getElementById(this.options.addButtonId);
    this.closeButton = document.getElementById(this.options.closeButtonId);
    this.cancelButton = document.getElementById(this.options.cancelButtonId);
    this.saveButton = document.getElementById(this.options.saveButtonId);
    this.categoryIdInput = document.getElementById(
        this.options.categoryIdInputId);
    this.categorySelect = document.getElementById(
        this.options.categorySelectId);

    this.init();
  }

  /**
   * 초기화
   */
  init() {
    // 카테고리 ID 입력 필드에 정규식 패턴 적용 (영문 소문자, 숫자, 하이픈만 허용)
    if (this.categoryIdInput) {
      this.categoryIdInput.addEventListener('input',
          this.validateCategoryId.bind(this));
    }

    // 모달 열기 버튼
    if (this.addButton) {
      this.addButton.addEventListener('click', this.openModal.bind(this));
    }

    // 모달 닫기 버튼
    if (this.closeButton) {
      this.closeButton.addEventListener('click', this.closeModal.bind(this));
    }

    // 모달 취소 버튼
    if (this.cancelButton) {
      this.cancelButton.addEventListener('click', this.closeModal.bind(this));
    }

    // 카테고리 저장 버튼
    if (this.saveButton) {
      this.saveButton.addEventListener('click', this.saveCategory.bind(this));
    }

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && this.isModalOpen()) {
        this.closeModal();
      }
    });

    // 모달 외부 클릭 시 닫기
    if (this.modal) {
      this.modal.addEventListener('click', (e) => {
        if (e.target === this.modal) {
          this.closeModal();
        }
      });
    }
  }

  /**
   * 카테고리 ID 유효성 검사 및 포맷팅
   */
  validateCategoryId() {
    if (this.categoryIdInput) {
      this.categoryIdInput.value = this.categoryIdInput.value.toLowerCase().replace(
          /[^a-z0-9-]/g, '');
    }
  }

  /**
   * 모달 열기
   */
  openModal() {
    if (this.modal) {
      this.modal.classList.remove('hidden');
      document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
    }
  }

  /**
   * 모달 닫기
   */
  closeModal() {
    if (this.modal) {
      this.modal.classList.add('hidden');
      document.body.style.overflow = ''; // 배경 스크롤 복원

      // 폼 초기화
      if (this.form) {
        this.form.reset();
      }
    }
  }

  /**
   * 모달이 열려있는지 확인
   * @returns {boolean} 모달 오픈 상태
   */
  isModalOpen() {
    return this.modal && !this.modal.classList.contains('hidden');
  }

  /**
   * 카테고리 폼 데이터 수집
   * @returns {Object} 카테고리 데이터
   */
  collectFormData() {
    const categoryId = document.getElementById(
        this.options.categoryIdInputId).value.trim();
    const displayName = document.getElementById(
        this.options.displayNameInputId).value.trim();
    const description = document.getElementById(
        this.options.descriptionInputId).value.trim();
    const imageUrl = document.getElementById(
        this.options.categoryImageUrlInputId).value.trim();

    return {
      id: categoryId,
      displayName: displayName,
      description: description || null,
      imageUrl: imageUrl || null
    };
  }

  /**
   * 카테고리 데이터 유효성 검사
   * @param {Object} data - 카테고리 데이터
   * @returns {boolean} 유효성 검사 통과 여부
   */
  validateFormData(data) {
    if (!data.id) {
      this.showToast('카테고리 ID를 입력해주세요.', true);
      return false;
    }

    if (!data.displayName) {
      this.showToast('표시 이름을 입력해주세요.', true);
      return false;
    }

    return true;
  }

  /**
   * 카테고리 저장
   */
  async saveCategory() {
    // 카테고리 데이터 수집
    const categoryData = this.collectFormData();

    // 유효성 검사
    if (!this.validateFormData(categoryData)) {
      return;
    }

    try {
      // 저장 버튼 비활성화 (중복 클릭 방지)
      if (this.saveButton) {
        this.saveButton.disabled = true;
        this.saveButton.classList.add('opacity-50');
      }

      // API 호출하여 카테고리 추가
      const response = await fetch('/api/categories', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(categoryData)
      });

      // 응답 처리
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || '카테고리 추가에 실패했습니다.');
      }

      const data = await response.json();

      // 모달 닫기
      this.closeModal();

      // 카테고리 선택 목록 업데이트
      if (this.categorySelect) {
        const option = document.createElement('option');
        option.value = data.id;
        option.textContent = data.displayName;
        this.categorySelect.appendChild(option);

        // 새 카테고리 선택
        this.categorySelect.value = data.id;
      }

      // 성공 메시지 표시
      this.showToast('카테고리가 성공적으로 추가되었습니다.');
    } catch (error) {
      console.error('Error:', error);
      this.showToast(error.message || '카테고리 추가에 실패했습니다.', true);
    } finally {
      // 저장 버튼 다시 활성화
      if (this.saveButton) {
        this.saveButton.disabled = false;
        this.saveButton.classList.remove('opacity-50');
      }
    }
  }

  /**
   * 토스트 메시지 표시
   * @param {string} message - 표시할 메시지
   * @param {boolean} isError - 오류 메시지 여부
   */
  showToast(message, isError = false) {
    // 이미 있는 토스트 제거
    const existingToast = document.querySelector('.toast-message');
    if (existingToast) {
      existingToast.remove();
    }

    // 새 토스트 생성
    const toast = document.createElement('div');
    toast.className = `fixed bottom-4 right-4 py-2 px-4 rounded-lg shadow-lg z-50 toast-message transition-opacity duration-300 ${
        isError ? 'bg-red-600' : 'bg-indigo-600'
    } text-white`;
    toast.textContent = message;

    document.body.appendChild(toast);

    // 3초 후 제거
    setTimeout(() => {
      toast.style.opacity = '0';
      setTimeout(() => {
        toast.remove();
      }, 300);
    }, 3000);
  }
}

// 문서 로드 완료 시 카테고리 매니저 인스턴스 생성
document.addEventListener('DOMContentLoaded', function () {
  new CategoryManager();
});

// 다른 스크립트에서 사용할 수 있도록 내보내기
window.CategoryManager = CategoryManager;