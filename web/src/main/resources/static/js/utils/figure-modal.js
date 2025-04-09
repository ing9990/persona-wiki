// 인물 추가 모달 관련 유틸리티

/**
 * 인물 추가 모달 관리자
 */
const FigureModal = (function () {
  // 모달 관련 DOM 요소
  let modal;
  let form;
  let imageUrlInput;
  let previewImage;
  let previewPlaceholder;
  let successAlert;
  let errorAlert;
  let errorMessage;

  // 모달 초기화
  function init() {
    // DOM 요소 참조 얻기
    modal = document.getElementById('figureAddModal');
    form = document.getElementById('modalFigureForm');
    imageUrlInput = document.getElementById('imageUrl');
    previewImage = document.getElementById('previewImage');
    previewPlaceholder = document.getElementById('previewPlaceholder');
    successAlert = document.getElementById('modalSuccessAlert');
    errorAlert = document.getElementById('modalErrorAlert');
    errorMessage = document.getElementById('modalErrorMessage');

    // 버튼 요소 참조
    const openModalButtons = document.querySelectorAll('.open-figure-modal');
    const closeModalBtn = document.getElementById('closeModal');
    const cancelBtn = document.getElementById('cancelButton');
    const submitBtn = document.getElementById('submitFigure');

    // 모달 열기 버튼 이벤트 리스너 등록
    if (openModalButtons && openModalButtons.length > 0) {
      openModalButtons.forEach(button => {
        button.addEventListener('click', function (e) {
          e.preventDefault();
          openModal();
        });
      });
    }

    if (closeModalBtn) {
      closeModalBtn.addEventListener('click', closeModal);
    }
    if (cancelBtn) {
      cancelBtn.addEventListener('click', closeModal);
    }
    if (submitBtn) {
      submitBtn.addEventListener('click', submitForm);
    }
    if (imageUrlInput) {
      imageUrlInput.addEventListener('input', updateImagePreview);
    }

    // 모달 외부 클릭 시 닫기
    if (modal) {
      modal.addEventListener('click', function (e) {
        if (e.target === modal) {
          closeModal();
        }
      });
    }

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape' && modal && !modal.classList.contains('hidden')) {
        closeModal();
      }
    });
  }

  // 모달 열기
  function openModal() {
    if (modal) {
      modal.classList.remove('hidden');
      modal.classList.add('modal-appear');
      document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
    }
  }

  // 모달 닫기
  function closeModal() {
    if (modal) {
      modal.classList.add('hidden');
      document.body.style.overflow = ''; // 스크롤 다시 활성화
      resetForm();
    }
  }

  // 폼 초기화
  function resetForm() {
    if (form) {
      form.reset();
    }
    if (successAlert) {
      successAlert.classList.add('hidden');
    }
    if (errorAlert) {
      errorAlert.classList.add('hidden');
    }
    if (previewImage) {
      previewImage.classList.add('hidden');
    }
    if (previewPlaceholder) {
      previewPlaceholder.classList.remove('hidden');
    }
  }

  // 이미지 미리보기 업데이트
  function updateImagePreview() {
    if (!imageUrlInput || !previewImage || !previewPlaceholder) {
      return;
    }

    const imageUrl = imageUrlInput.value.trim();

    if (imageUrl) {
      previewImage.src = imageUrl;
      previewImage.onload = function () {
        previewImage.classList.remove('hidden');
        previewPlaceholder.classList.add('hidden');
      };

      previewImage.onerror = function () {
        previewImage.classList.add('hidden');
        previewPlaceholder.classList.remove('hidden');
      };
    } else {
      previewImage.classList.add('hidden');
      previewPlaceholder.classList.remove('hidden');
    }
  }

  // 인물 등록 폼 제출
  // 인물 등록 폼 제출
  async function submitForm() {
    if (!form) {
      return;
    }

    // 폼 데이터 수집
    const formData = {
      figureName: document.getElementById('figureName').value.trim(),
      categoryId: document.getElementById('categoryId').value.trim()
          || 'default',
      imageUrl: document.getElementById('imageUrl').value.trim(),
      bio: document.getElementById('bio').value.trim()
    };

    // 필수 필드 검증
    if (!formData.figureName || !formData.bio) {
      showError('인물 이름과 소개는 필수 입력 항목입니다.');
      return;
    }

    // 패턴 검증 추가
    const namePattern = /^([가-힣]{1,20}|[a-zA-Z]{1,20})$/;
    if (!namePattern.test(formData.figureName)) {
      showError('이름은 한글만 또는 영어만 사용할 수 있으며, 1~20자 이내여야 합니다.');
      return;
    }

    // 로딩 스피너 표시
    showLoading(true);

    try {
      // 폼을 직접 생성하여 제출
      const formElement = document.createElement('form');
      formElement.method = 'POST';
      formElement.action = '/figures';
      formElement.style.display = 'none';

      // 폼 필드 추가
      Object.keys(formData).forEach(key => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = formData[key];
        formElement.appendChild(input);
      });

      // body에 폼 추가 및 제출
      document.body.appendChild(formElement);
      formElement.submit();

    } catch (error) {
      // 로딩 스피너 숨기기
      showLoading(false);

      // 오류 메시지 표시
      let errorMsg = error.message || '인물 추가 중 오류가 발생했습니다.';
      if (errorMsg.includes("did not match the expected pattern")) {
        errorMsg = "이름은 한글만 또는 영어만 사용할 수 있으며, 1~20자 이내여야 합니다.";
      }

      showError(errorMsg);

      // 토스트 메시지 표시 (있는 경우)
      if (window.toastManager) {
        window.toastManager.error(errorMsg);
      }
    }
  }

// 로딩 스피너 표시/숨기기
  function showLoading(isLoading) {
    const submitBtn = document.getElementById('submitFigure');
    const loadingSpinner = document.getElementById('loadingSpinner');

    if (isLoading) {
      // 버튼 비활성화 및 로딩 스피너 표시
      if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.classList.add('opacity-50', 'cursor-not-allowed');
        submitBtn.innerHTML = `
        <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        처리 중...
      `;
      }
    } else {
      // 버튼 활성화 및 로딩 스피너 숨기기
      if (submitBtn) {
        submitBtn.disabled = false;
        submitBtn.classList.remove('opacity-50', 'cursor-not-allowed');
        submitBtn.innerHTML = '인물 등록';
      }
    }
  }

  // 오류 메시지 표시
  function showError(message) {
    if (errorAlert && errorMessage && successAlert) {
      errorMessage.textContent = message;
      errorAlert.classList.remove('hidden');
      successAlert.classList.add('hidden');
    }
  }

  // 공개 API
  return {
    init, openModal, closeModal
  };
})();

// DOM이 로드된 후 모달 초기화
document.addEventListener('DOMContentLoaded', FigureModal.init);

// 전역 스코프에서 접근 가능하도록 설정
window.FigureModal = FigureModal;