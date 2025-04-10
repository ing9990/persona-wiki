/**
 * 인물 추가 다단계 모달 스크립트
 */
document.addEventListener('DOMContentLoaded', () => {
  // 모달 요소
  const figureAddModal = document.getElementById('figureAddModal');
  const figureModalContent = document.getElementById('figureModalContent');
  const modalTitle = document.getElementById('modalTitle');

  // 모달 스텝
  const stepCategory = document.getElementById('stepCategory');
  const stepName = document.getElementById('stepName');
  const stepConfirm = document.getElementById('stepConfirm');
  const stepFailure = document.getElementById('stepFailure');

  // 스텝별 버튼 그룹
  const stepCategoryButtons = document.getElementById('stepCategoryButtons');
  const stepNameButtons = document.getElementById('stepNameButtons');
  const stepConfirmButtons = document.getElementById('stepConfirmButtons');
  const stepFailureButtons = document.getElementById('stepFailureButtons');

  // 폼 요소
  const figureForm = document.getElementById('figureForm');
  const formFigureName = document.getElementById('formFigureName');
  const formCategoryId = document.getElementById('formCategoryId');
  const formImageUrl = document.getElementById('formImageUrl');
  const formBio = document.getElementById('formBio');

  // 입력 및 표시 요소
  const selectedCategory = document.getElementById('selectedCategory');
  const figureName = document.getElementById('figureName');
  const figureImage = document.getElementById('figureImage');
  const figureSummaryName = document.getElementById('figureSummaryName');
  const figureSummary = document.getElementById('figureSummary');
  const nameSearchLoading = document.getElementById('nameSearchLoading');

  // 알림 관련 요소
  const modalAlertContainer = document.getElementById('modalAlertContainer');
  const modalAlert = document.getElementById('modalAlert');
  const alertIcon = document.getElementById('alertIcon');
  const modalAlertMessage = document.getElementById('modalAlertMessage');

  // 버튼 요소
  const searchFigureBtn = document.getElementById('searchFigure');
  const backToCategoriesBtn = document.getElementById('backToCategories');
  const submitFigureBtn = document.getElementById('submitFigure');
  const notThisFigureBtn = document.getElementById('notThisFigure');
  const contactButton = document.getElementById('contactButton');
  const closeFailureBtn = document.getElementById('closeFailure');
  const closeModalBtn = document.getElementById('closeModal');
  const cancelButton = document.getElementById('cancelButton');

  // 상태 관리
  let currentStep = 1;
  let selectedCategoryId = '';
  let selectedCategoryName = '';

  // 이름 유효성 검사 정규식
  const nameRegex = /^([가-힣]{1,20}|[a-zA-Z]{1,20})$/;

  // 알림 메시지 표시 함수
  function showAlert(message, type = 'error') {
    modalAlertMessage.textContent = message;

    // 기존 클래스 제거
    modalAlert.className = 'p-4 rounded-md';
    alertIcon.className = 'fa-solid mt-0.5 mr-2';

    // 타입에 따른 스타일 적용
    if (type === 'error') {
      modalAlert.classList.add('bg-red-100', 'border-l-4', 'border-red-500', 'text-red-700');
      alertIcon.classList.add('fa-exclamation-circle', 'text-red-500');
    } else if (type === 'success') {
      modalAlert.classList.add('bg-green-100', 'border-l-4', 'border-green-500', 'text-green-700');
      alertIcon.classList.add('fa-check-circle', 'text-green-500');
    } else if (type === 'info') {
      modalAlert.classList.add('bg-blue-100', 'border-l-4', 'border-blue-500', 'text-blue-700');
      alertIcon.classList.add('fa-info-circle', 'text-blue-500');
    } else if (type === 'warning') {
      modalAlert.classList.add('bg-amber-100', 'border-l-4', 'border-amber-500', 'text-amber-700');
      alertIcon.classList.add('fa-exclamation-triangle', 'text-amber-500');
    }

    // 알림 표시
    modalAlertContainer.classList.remove('hidden');

    // 3초 후 자동으로 사라짐
    setTimeout(() => {
      modalAlertContainer.classList.add('hidden');
    }, 3000);
  }

  // 모달 열기 함수 (전역 함수로 노출)
  window.openFigureModal = function() {
    // 모달 리셋
    resetModal();

    // 모달 표시
    figureAddModal.classList.remove('hidden');
    setTimeout(() => {
      figureModalContent.classList.remove('scale-95', 'opacity-0');
      figureModalContent.classList.add('scale-100', 'opacity-100');
    }, 10);
  };

  // 모달 닫기 함수
  function closeModal() {
    figureModalContent.classList.remove('scale-100', 'opacity-100');
    figureModalContent.classList.add('scale-95', 'opacity-0');

    setTimeout(() => {
      figureAddModal.classList.add('hidden');
      resetModal();
    }, 300);
  }

  // 모달 리셋 함수
  function resetModal() {
    // 스텝 초기화
    goToStep(1);

    // 입력값 초기화
    figureName.value = '';
    selectedCategoryId = '';
    selectedCategoryName = '';

    // 알림 숨기기
    modalAlertContainer.classList.add('hidden');

    // 로딩 상태 숨기기
    nameSearchLoading.classList.add('hidden');
  }

  // 스텝 변경 함수
  function goToStep(step) {
    currentStep = step;

    // 모든 스텝 숨기기
    stepCategory.classList.add('hidden');
    stepName.classList.add('hidden');
    stepConfirm.classList.add('hidden');
    stepFailure.classList.add('hidden');

    // 모든 버튼 그룹 숨기기
    stepCategoryButtons.classList.add('hidden');
    stepNameButtons.classList.add('hidden');
    stepConfirmButtons.classList.add('hidden');
    stepFailureButtons.classList.add('hidden');

    // 스텝별 타이틀 및 표시 설정
    switch(step) {
      case 1: // 카테고리 선택 스텝
        modalTitle.textContent = '인물 추가';
        stepCategory.classList.remove('hidden');
        stepCategoryButtons.classList.remove('hidden');
        break;

      case 2: // 인물 이름 입력 스텝
        modalTitle.textContent = '인물 이름 입력';
        stepName.classList.remove('hidden');
        stepNameButtons.classList.remove('hidden');

        // 선택된 카테고리 표시
        selectedCategory.textContent = selectedCategoryName;

        // 이름 입력란에 포커스
        setTimeout(() => figureName.focus(), 100);
        break;

      case 3: // 인물 확인 스텝
        modalTitle.textContent = '인물 확인';
        stepConfirm.classList.remove('hidden');
        stepConfirmButtons.classList.remove('hidden');
        break;

      case 4: // 등록 실패 안내 스텝
        modalTitle.textContent = '인물 찾기 실패';
        stepFailure.classList.remove('hidden');
        stepFailureButtons.classList.remove('hidden');
        break;
    }
  }

  // 카테고리 선택 이벤트 핸들러
  document.querySelectorAll('.category-button').forEach(button => {
    button.addEventListener('click', () => {
      selectedCategoryId = button.dataset.categoryId;
      selectedCategoryName = button.dataset.categoryName;

      goToStep(2);
    });
  });

  // 인물 이름 검색 이벤트 핸들러
  searchFigureBtn.addEventListener('click', searchFigureHandler);

  // 인물 이름 검색 함수
  async function searchFigureHandler() {
    const name = figureName.value.trim();

    // 유효성 검사: 빈 값 체크
    if (!name) {
      showAlert('인물 이름을 입력해주세요', 'error');
      figureName.focus();
      return;
    }

    // 유효성 검사: 정규식 패턴 체크 (한글 또는 영문 1~20자)
    if (!nameRegex.test(name)) {
      showAlert('이름은 한글 또는 영어만 사용할 수 있으며, 1~20자 이내여야 합니다.', 'error');
      figureName.focus();
      return;
    }

    // 로딩 상태 표시 & 버튼 비활성화
    nameSearchLoading.classList.remove('hidden');
    searchFigureBtn.disabled = true;
    backToCategoriesBtn.disabled = true;

    try {
      // 위키피디아 인물 검색 API 호출
      const response = await fetch(`/api/v1/search-images/wikipedia?figureName=${encodeURIComponent(name)}`);
      const data = await response.json();

      // 로딩 상태 숨기기 & 버튼 활성화
      nameSearchLoading.classList.add('hidden');
      searchFigureBtn.disabled = false;
      backToCategoriesBtn.disabled = false;

      // 결과 확인 및 처리
      if (response.ok && data && data.imageUrl && data.summary) {
        // 인물 정보 표시
        figureImage.src = data.imageUrl;
        figureSummaryName.textContent = name;
        figureSummary.textContent = data.summary;

        // 폼 값 설정
        formFigureName.value = name;
        formCategoryId.value = selectedCategoryId;
        formImageUrl.value = data.imageUrl;
        formBio.value = data.summary;

        // 스텝 3으로 이동 (인물 확인)
        goToStep(3);
      } else {
        // 스텝 4로 이동 (검색 실패)
        goToStep(4);
      }
    } catch (error) {
      console.error('인물 검색 오류:', error);

      // 로딩 상태 숨기기 & 버튼 활성화
      nameSearchLoading.classList.add('hidden');
      searchFigureBtn.disabled = false;
      backToCategoriesBtn.disabled = false;

      // 오류 메시지 표시
      showAlert('인물 검색 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', 'error');
    }
  }

  // 이전 버튼 (이름 입력 -> 카테고리 선택)
  backToCategoriesBtn.addEventListener('click', () => {
    goToStep(1);
  });

  // "아닌데요?" 버튼 (인물 확인 -> 검색 실패)
  notThisFigureBtn.addEventListener('click', () => {
    goToStep(4);
  });

  // 인물 등록 제출 버튼
  submitFigureBtn.addEventListener('click', () => {
    // 최종 유효성 검사
    if (!formFigureName.value || !formCategoryId.value || !formImageUrl.value || !formBio.value) {
      showAlert('필수 정보가 누락되었습니다. 다시 시도해주세요.', 'error');
      return;
    }

    // 폼 제출
    figureForm.submit();
  });

  // 채널톡 문의 버튼
  contactButton.addEventListener('click', () => {
    // 채널톡 열기 (실제 구현시 채널톡 API 연동 필요)
    if (window.ChannelIO) {
      window.ChannelIO('openChat', {
        // 채널톡에 전달할 데이터
        profile: {
          name: figureName.value,
          mobileNumber: ''
        },
        // 초기 메시지
        message: `인물 등록 요청: ${figureName.value} (${selectedCategoryName})`
      });
    } else {
      // 채널톡이 없는 경우 새 창에서 이메일로 연결
      window.open('mailto:contact@국민사형투표.com?subject=인물 등록 요청&body=등록 요청 인물: ' + figureName.value);
    }
  });

  // Enter 키로 검색 실행 (스텝 2에서)
  figureName.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && currentStep === 2) {
      e.preventDefault();
      searchFigureHandler();
    }
  });

  // 모달 닫기 이벤트들
  closeModalBtn.addEventListener('click', closeModal);
  cancelButton.addEventListener('click', closeModal);
  closeFailureBtn.addEventListener('click', closeModal);

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