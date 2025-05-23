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
   */
  constructor(options) {
    this.options = {
      modalId: 'categoryModal',
      formId: 'categoryForm',
      addButtonId: 'addCategoryBtn',
      closeButtonId: 'closeModalBtn',
      cancelButtonId: 'cancelModalBtn',
      saveButtonId: 'saveCategoryBtn',
      categoryIdInputId: 'categoryIdModal',   // 모달 ID 필드
      displayNameInputId: 'displayNameModal', // 모달 표시 이름 필드
      descriptionInputId: 'descriptionModal', // 모달 설명 필드
      categoryImageUrlInputId: 'categoryImageUrlModal', // 모달 이미지 URL 필드
      categorySelectId: 'categoryId',  // 인물 추가 폼의 카테고리 선택 드롭다운
      ...options
    };

    console.log('CategoryManager initializing with options:', this.options);

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

    console.log("CategoryManager initialized with elements:", {
      modal: this.modal,
      form: this.form,
      addButton: this.addButton,
      closeButton: this.closeButton,
      cancelButton: this.cancelButton,
      saveButton: this.saveButton,
      categoryIdInput: this.categoryIdInput,
      categorySelect: this.categorySelect
    });

    // 모달 요소 확인 또는 생성
    this.ensureModalExists();

    this.init();
  }

  /**
   * 모달 요소 확인 또는 생성
   */
  ensureModalExists() {
    if (!this.modal) {
      console.log('Modal not found, creating it dynamically');

      // 기존 모달 제거 (혹시 모를 중복 방지)
      const existingModal = document.getElementById(this.options.modalId);
      if (existingModal) {
        existingModal.remove();
      }

      // 새 모달 생성
      const modalHTML = `
        <div id="${this.options.modalId}" class="fixed inset-0 bg-gray-600 bg-opacity-50 z-50 flex items-center justify-center hidden">
          <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
            <!-- 모달 헤더 -->
            <div class="flex items-center justify-between p-5 border-b border-gray-200">
              <h3 class="text-xl font-bold text-gray-800">
                <i class="fas fa-folder-plus text-indigo-500 mr-2"></i>
                새 카테고리 추가
              </h3>
              <button type="button" id="${this.options.closeButtonId}" class="text-gray-500 hover:text-gray-700">
                <i class="fas fa-times text-xl"></i>
              </button>
            </div>

            <!-- 모달 본문 -->
            <div class="p-5">
              <form id="${this.options.formId}" class="space-y-4">
                <!-- 카테고리 ID -->
                <div>
                  <label for="${this.options.categoryIdInputId}"
                         class="block text-sm font-medium text-gray-700 mb-1 required-label">카테고리 ID</label>
                  <input type="text" id="${this.options.categoryIdInputId}" required
                         class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                         placeholder="영문 소문자, 숫자, 하이픈만 사용 (예: k-pop, sports)">
                  <p class="mt-1 text-xs text-gray-500">고유 식별자로 사용됩니다. 변경이 어려우니 신중히 입력해주세요.</p>
                </div>

                <!-- 표시 이름 -->
                <div>
                  <label for="${this.options.displayNameInputId}"
                         class="block text-sm font-medium text-gray-700 mb-1 required-label">표시 이름</label>
                  <input type="text" id="${this.options.displayNameInputId}" required
                         class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                         placeholder="실제로 표시될 이름 (예: K-POP, 스포츠)">
                </div>

                <!-- 설명 -->
                <div>
                  <label for="${this.options.descriptionInputId}"
                         class="block text-sm font-medium text-gray-700 mb-1">설명</label>
                  <textarea id="${this.options.descriptionInputId}" rows="3"
                            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                            placeholder="카테고리에 대한 간략한 설명을 입력하세요"></textarea>
                </div>

                <!-- 이미지 URL -->
                <div>
                  <label for="${this.options.categoryImageUrlInputId}" class="block text-sm font-medium text-gray-700 mb-1">이미지
                    URL</label>
                  <input type="url" id="${this.options.categoryImageUrlInputId}"
                         class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
                         placeholder="https://example.com/category-image.jpg">
                </div>
              </form>
            </div>

            <!-- 모달 푸터 -->
            <div class="flex justify-end p-5 border-t border-gray-200 space-x-3">
              <button type="button" id="${this.options.cancelButtonId}"
                      class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 transition">
                취소
              </button>
              <button type="button" id="${this.options.saveButtonId}"
                      class="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 transition flex items-center">
                <i class="fas fa-save mr-1"></i>
                저장
              </button>
            </div>
          </div>
        </div>
      `;

      // 모달을 body에 추가
      document.body.insertAdjacentHTML('beforeend', modalHTML);

      // 참조 업데이트
      this.modal = document.getElementById(this.options.modalId);
      this.form = document.getElementById(this.options.formId);
      this.closeButton = document.getElementById(this.options.closeButtonId);
      this.cancelButton = document.getElementById(this.options.cancelButtonId);
      this.saveButton = document.getElementById(this.options.saveButtonId);
      this.categoryIdInput = document.getElementById(
          this.options.categoryIdInputId);

      console.log('Modal created dynamically, elements now:', {
        modal: this.modal,
        form: this.form,
        closeButton: this.closeButton,
        cancelButton: this.cancelButton,
        saveButton: this.saveButton,
        categoryIdInput: this.categoryIdInput
      });
    }
  }

  /**
   * 초기화
   */
  init() {
    console.log('Initializing CategoryManager event listeners');

    // 카테고리 ID 입력 필드에 정규식 패턴 적용 (영문 소문자, 숫자, 하이픈만 허용)
    if (this.categoryIdInput) {
      this.categoryIdInput.addEventListener('input',
          this.validateCategoryId.bind(this));
    } else {
      console.error('Category ID input not found. Check the ID:',
          this.options.categoryIdInputId);
    }

    // 모달 열기 버튼
    if (this.addButton) {
      this.addButton.addEventListener('click', this.openModal.bind(this));
      console.log('Add button click listener added');
    } else {
      console.error('Add button not found. Check the ID:',
          this.options.addButtonId);
    }

    // 모달 닫기 버튼
    if (this.closeButton) {
      this.closeButton.addEventListener('click', this.closeModal.bind(this));
      console.log('Close button click listener added');
    } else {
      console.error('Close button not found. Check the ID:',
          this.options.closeButtonId);
    }

    // 모달 취소 버튼
    if (this.cancelButton) {
      this.cancelButton.addEventListener('click', this.closeModal.bind(this));
      console.log('Cancel button click listener added');
    } else {
      console.error('Cancel button not found. Check the ID:',
          this.options.cancelButtonId);
    }

    // 카테고리 저장 버튼
    if (this.saveButton) {
      this.saveButton.addEventListener('click', this.saveCategory.bind(this));
      console.log('Save button click listener added');
    } else {
      console.error('Save button not found. Check the ID:',
          this.options.saveButtonId);
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
      console.log('Modal click-outside listener added');
    } else {
      console.error('Modal not found. Check the ID:', this.options.modalId);
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
    console.log('Opening modal');
    if (this.modal) {
      this.modal.classList.remove('hidden');
      document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
    } else {
      console.error('Cannot open modal: modal element not found');
    }
  }

  /**
   * 모달 닫기
   */
  closeModal() {
    console.log('Closing modal');
    if (this.modal) {
      this.modal.classList.add('hidden');
      document.body.style.overflow = ''; // 배경 스크롤 복원

      // 폼 초기화
      if (this.form) {
        this.form.reset();
      }
    } else {
      console.error('Cannot close modal: modal element not found');
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
    console.log('Collecting form data');

    const categoryId = document.getElementById(
        this.options.categoryIdInputId)?.value?.trim();
    const displayName = document.getElementById(
        this.options.displayNameInputId)?.value?.trim();
    const description = document.getElementById(
        this.options.descriptionInputId)?.value?.trim();
    const imageUrl = document.getElementById(
        this.options.categoryImageUrlInputId)?.value?.trim();

    console.log('Form data collected:',
        {categoryId, displayName, description, imageUrl});

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
    console.log('Validating form data:', data);

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
    console.log('Saving category');

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

      console.log('Sending API request with data:', categoryData);

      // API 호출하여 카테고리 추가
      const response = await fetch('/api/categories', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(categoryData)
      });

      console.log('API response status:', response.status);

      // 응답 처리
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || '카테고리 추가에 실패했습니다.');
      }

      const data = await response.json();
      console.log('API response data:', data);

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
        console.log('Category select updated and new category selected:',
            data.id);
      } else {
        console.error('Cannot update category select: element not found');
      }

      // 성공 메시지 표시
      this.showToast('카테고리가 성공적으로 추가되었습니다.');
    } catch (error) {
      console.error('Error saving category:', error);
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
    console.log('Showing toast:', message, 'isError:', isError);

    // ToastManager 존재 여부 확인
    if (window.ToastManager) {
      window.ToastManager.show(message, isError);
      return;
    }

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
let categoryManager;
document.addEventListener('DOMContentLoaded', function () {
  console.log('Document loaded, initializing CategoryManager');
  // DOM이 완전히 렌더링될 때까지 약간의 지연 추가
  setTimeout(() => {
    categoryManager = new CategoryManager();
  }, 300);
});

// 다른 스크립트에서 사용할 수 있도록 내보내기
window.CategoryManager = CategoryManager;