// figure-modal.js

document.addEventListener('DOMContentLoaded', () => {
  const imageUrlInput = document.getElementById('imageUrl');
  const previewImage = document.getElementById('previewImage');
  const previewPlaceholder = document.getElementById('previewPlaceholder');
  const previewContainer = document.getElementById('previewContainer');
  const imageZoomModal = document.getElementById('imageZoomModal');
  const zoomedImage = document.getElementById('zoomedImage');
  const errorAlert = document.getElementById('modalErrorAlert');
  const errorMessage = document.getElementById('modalErrorMessage');

  const figureModal = document.getElementById('figureAddModal');
  const modalContent = document.getElementById('figureModalContent');
  const closeModalBtn = document.getElementById('closeModal');
  const cancelBtn = document.getElementById('cancelButton');
  const submitBtn = document.getElementById('submitFigure');

  // 로딩 스피너 생성
  const loadingOverlay = document.createElement('div');
  loadingOverlay.className = 'absolute inset-0 bg-white bg-opacity-75 flex items-center justify-center z-10 hidden';
  loadingOverlay.innerHTML = '<div class="animate-spin rounded-full h-8 w-8 border-t-2 border-indigo-600"></div>';
  modalContent.appendChild(loadingOverlay);

  function showPreview(url) {
    previewImage.src = url;
    previewImage.classList.remove('hidden');
    previewPlaceholder.classList.add('hidden');
  }

  function resetPreview() {
    previewImage.src = '';
    previewImage.classList.add('hidden');
    previewPlaceholder.classList.remove('hidden');
  }

  imageUrlInput.addEventListener('input', () => {
    const url = imageUrlInput.value;
    if (!url || !/^https?:\/\//.test(url)) {
      resetPreview();
      return;
    }
    previewImage.src = url;
    previewImage.onload = () => showPreview(url);
    previewImage.onerror = resetPreview;
  });

  if (previewContainer && imageZoomModal && zoomedImage) {
    previewContainer.addEventListener('click', () => {
      if (!previewImage.classList.contains('hidden')) {
        zoomedImage.src = previewImage.src;
        imageZoomModal.classList.remove('hidden');
      }
    });

    imageZoomModal.addEventListener('click', () => {
      imageZoomModal.classList.add('hidden');
      zoomedImage.src = '';
    });
  }

  function closeModal() {
    figureModal.classList.add('hidden');
    document.getElementById('modalFigureForm')?.reset();
    resetPreview();
    errorAlert.classList.add('hidden');
  }

  closeModalBtn.addEventListener('click', closeModal);
  cancelBtn.addEventListener('click', closeModal);

  // ESC 키로 모달 닫기
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && !figureModal.classList.contains('hidden')) {
      closeModal();
    }
  });

  // open-figure-modal 클래스를 가진 요소 클릭 시 모달 열기
  document.querySelectorAll('.open-figure-modal').forEach(el => {
    el.addEventListener('click', () => {
      figureModal.classList.remove('hidden');
      modalContent.classList.remove('opacity-0', 'scale-95');
      modalContent.classList.add('opacity-100', 'scale-100');
    });
  });

  submitBtn.addEventListener('click', async () => {
    errorAlert.classList.add('hidden');
    loadingOverlay.classList.remove('hidden');
    submitBtn.disabled = true;
    submitBtn.classList.add('opacity-50', 'cursor-not-allowed');

    const formData = {
      figureName: document.getElementById('figureName').value.trim(),
      categoryId: document.getElementById('categoryId').value.trim()
          || 'default',
      imageUrl: document.getElementById('imageUrl').value.trim(),
      bio: document.getElementById('bio').value.trim()
    };

    try {
      const res = await fetch('/figures', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      if (!res.ok) {
        throw new Error('인물 추가에 실패했습니다.');
      }

      const redirectUrl = await res.text();
      window.location.href = redirectUrl;

    } catch (error) {
      loadingOverlay.classList.add('hidden');
      let errorMsg = error.message || '인물 추가 중 오류가 발생했습니다.';
      if (errorMsg.includes("did not match the expected pattern")) {
        errorMsg = "이름은 한글만 또는 영어만 사용할 수 있으며, 1~20자 이내여야 합니다.";
      }
      errorMessage.textContent = errorMsg;
      errorAlert.classList.remove('hidden');
      submitBtn.disabled = false;
      submitBtn.classList.remove('opacity-50', 'cursor-not-allowed');
    }
  });
});