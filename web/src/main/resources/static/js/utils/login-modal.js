// 인물 추가 모달 관련 유틸리티

/**
 * 인물 추가 모달 관리자
 */
const FigureModal = (function() {
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
    const openModalBtn = document.getElementById('openFigureModal');
    const closeModalBtn = document.getElementById('closeModal');
    const cancelBtn = document.getElementById('cancelButton');
    const submitBtn = document.getElementById('submitFigure');

    // 이벤트 리스너 등록
    if (openModalBtn) openModalBtn.addEventListener('click', openModal);
    if (closeModalBtn) closeModalBtn.addEventListener('click', closeModal);
    if (cancelBtn) cancelBtn.addEventListener('click', closeModal);
    if (submitBtn) submitBtn.addEventListener('click', submitForm);
    if (imageUrlInput) imageUrlInput.addEventListener('input', updateImagePreview);

    // 모달 외부 클릭 시 닫기
    if (modal) {
      modal.addEventListener('click', function(e) {
        if (e.target === modal) {
          closeModal();
        }
      });
    }

    // ESC 키로 모달 닫기
    document.addEventListener('keydown', function(e) {
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
    if (form) form.reset();
    if (successAlert) successAlert.classList.add('hidden');
    if (errorAlert) errorAlert.classList.add('hidden');
    if (previewImage) previewImage.classList.add('hidden');
    if (previewPlaceholder) previewPlaceholder.classList.remove('hidden');
  }

  // 이미지 미리보기 업데이트
  function updateImagePreview() {
    if (!imageUrlInput || !previewImage || !previewPlaceholder) return;

    const imageUrl = imageUrlInput.value.trim();

    if (imageUrl) {
      previewImage.src = imageUrl;
      previewImage.onload = function() {
        previewImage.classList.remove('hidden');
        previewPlaceholder.classList.add('hidden');
      };

      previewImage.onerror = function() {
        previewImage.classList.add('hidden');
        previewPlaceholder.classList.remove('hidden');
      };
    } else {
      previewImage.classList.add('hidden');
      previewPlaceholder.classList.remove('hidden');
    }
  }

  // 인물 등록 폼 제출
  async function submitForm() {
    if (!form) return;

    // 폼 데이터 수집
    const formData = {
      figureName: document.getElementById('figureName').value.trim(),
      categoryId: document.getElementById('categoryId').value.trim(),
      imageUrl: document.getElementById('imageUrl').value.trim(),
      bio: document.getElementById('bio').value.trim()
    };

    // 필수 필드 검증
    if (!formData.figureName || !formData.bio) {
      showError('인물 이름과 소개는 필수 입력 항목입니다.');
      return;
    }

    try {
      // API 호출 (FigureApi 활용)
      const result = await window.FigureApi.createFigure(formData);

      // 성공 처리
      showSuccess();

      // 토스트 메시지 표시 (있는 경우)
      if (window.toastManager) {
        window.toastManager.success('인물이 성공적으로 추가되었습니다!');
      }

      // 모달 닫기 전 짧은 지연 설정
      setTimeout(() => {
        closeModal();

        // 새로 추가된 인물 페이지로 리다이렉트
        if (result.redirectUrl) {
          window.location.href = result.redirectUrl;
        }
      }, 1500);
    } catch (error) {
      // 오류 처리
      showError(error.message || '인물 추가 중 오류가 발생했습니다.');

      // 토스트 메시지 표시 (있는 경우)
      if (window.toastManager) {
        window.toastManager.error(error.message || '인물 추가 중 오류가 발생했습니다.');
      }
    }
  }

  // 성공 메시지 표시
  function showSuccess() {
    if (successAlert && errorAlert) {
      successAlert.classList.remove('hidden');
      errorAlert.classList.add('hidden');
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
    init,
    openModal,
    closeModal
  };
})();

// DOM이 로드된 후 모달 초기화
document.addEventListener('DOMContentLoaded', FigureModal.init);

// 전역 스코프에서 접근 가능하도록 설정
window.FigureModal = FigureModal;

// ES 모듈로 사용하기 위해 export
export default FigureModal;