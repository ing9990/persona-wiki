document.addEventListener('DOMContentLoaded', () => {
  // 모달 관련 요소
  const figureAddModal = document.getElementById('figureAddModal');
  const figureModalContent = document.getElementById('figureModalContent');
  const closeModalBtn = document.getElementById('closeModal');
  const cancelButton = document.getElementById('cancelButton');

  // 폼 요소
  const figureForm = document.getElementById('modalFigureForm');
  const submitButton = document.getElementById('submitFigure');

  // 입력 필드
  const figureNameInput = document.getElementById('figureName');
  const categoryIdSelect = document.getElementById('categoryId');
  const imageUrlInput = document.getElementById('imageUrl');
  const bioInput = document.getElementById('bio');

  // 이미지 미리보기 요소
  const previewImage = document.getElementById('previewImage');
  const previewPlaceholder = document.getElementById('previewPlaceholder');
  const previewContainer = document.getElementById('previewContainer');
  const imageZoomModal = document.getElementById('imageZoomModal');
  const zoomedImage = document.getElementById('zoomedImage');

  // 유효성 검사 정규식 (CreateFigureRequest에서 가져온 패턴)
  const nameRegex = /^([가-힣]{1,20}|[a-zA-Z]{1,20})$/;
  // URL 정규식 - http 또는 https로 시작하는 URL
  const urlRegex = new RegExp(
      '^(https?://)\\w[\\w\\-._~:/?#\\[\\]@!$&\'()*+,;=]+$');

  // 유효성 검사 함수
  function validateForm() {
    // 이름 검사
    if (!figureNameInput.value.trim()) {
      toastManager.error('인물 이름은 필수입니다.');
      return false;
    }

    if (!nameRegex.test(figureNameInput.value.trim())) {
      toastManager.error('이름은 한글 또는 영어만 사용할 수 있으며, 1~20자 이내여야 합니다.');
      return false;
    }

    // 카테고리 검사 (창작물에서는 카테고리가 필수지만 UI에서는 선택사항으로 표시됨)
    if (categoryIdSelect.value === 'default') {
      toastManager.error('카테고리는 필수입니다.');
      return false;
    }

    // 이미지 URL 검사
    if (!imageUrlInput.value.trim()) {
      toastManager.error('이미지 URL은 필수입니다.');
      return false;
    }

    if (!urlRegex.test(imageUrlInput.value.trim())) {
      toastManager.error('올바른 URL 형식이 아닙니다. http 또는 https로 시작해야 합니다.');
      return false;
    }

    // 소개 검사
    if (!bioInput.value.trim()) {
      toastManager.error('소개글은 필수입니다.');
      return false;
    }

    return true;
  }

  // 이미지 URL 입력 시 미리보기 업데이트
  imageUrlInput.addEventListener('input', updateImagePreview);

  function updateImagePreview() {
    const url = imageUrlInput.value.trim();

    if (url && urlRegex.test(url)) {
      previewImage.src = url;
      previewImage.onload = () => {
        previewPlaceholder.classList.add('hidden');
        previewImage.classList.remove('hidden');
      };
      previewImage.onerror = () => {
        previewPlaceholder.classList.remove('hidden');
        previewImage.classList.add('hidden');
      };
    } else {
      previewPlaceholder.classList.remove('hidden');
      previewImage.classList.add('hidden');
    }
  }

  // 이미지 클릭 시 확대 모달 표시
  previewContainer.addEventListener('click', () => {
    if (!previewImage.classList.contains('hidden')) {
      zoomedImage.src = previewImage.src;
      imageZoomModal.classList.remove('hidden');
    }
  });

  // 확대 모달 클릭 시 닫기
  imageZoomModal.addEventListener('click', () => {
    imageZoomModal.classList.add('hidden');
  });

  // 폼 제출 이벤트
  submitButton.addEventListener('click', (e) => {
    e.preventDefault();

    if (validateForm()) {
      // 폼 제출 전 한 번 더 유효성 검사
      figureForm.submit();

      // 성공 토스트 표시
      toastManager.success('인물 등록 요청이 제출되었습니다.');
    }
  });

  // 모달 열기 함수 (다른 JS 파일에서 호출)
  window.openFigureModal = function () {
    // 폼 초기화
    figureForm.reset();
    previewPlaceholder.classList.remove('hidden');
    previewImage.classList.add('hidden');

    // 모달 표시
    figureAddModal.classList.remove('hidden');
    setTimeout(() => {
      figureModalContent.classList.remove('scale-95', 'opacity-0');
      figureModalContent.classList.add('scale-100', 'opacity-100');
    }, 10);

    // 안내 토스트 표시
    toastManager.info('인물 정보를 입력해주세요.');
  };

  function closeModal() {
    figureModalContent.classList.remove('scale-100', 'opacity-100');
    figureModalContent.classList.add('scale-95', 'opacity-0');

    setTimeout(() => {
      figureAddModal.classList.add('hidden');
      figureForm.reset();
      previewPlaceholder.classList.remove('hidden');
      previewImage.classList.add('hidden');
    }, 300);
  }

  // 모달 닫기 이벤트
  closeModalBtn.addEventListener('click', closeModal);
  cancelButton.addEventListener('click', closeModal);

  // 모달 외부 클릭 시 닫기
  figureAddModal.addEventListener('click', (e) => {
    if (e.target === figureAddModal) {
      closeModal();
    }
  });

  // ESC 키 누르면 모달 닫기
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && !figureAddModal.classList.contains('hidden')) {
      closeModal();
    }
  });
});