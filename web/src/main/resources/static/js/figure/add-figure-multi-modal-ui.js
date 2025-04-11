/**
 * 인물 추가 다단계 모달 UI 조작 스크립트
 * DOM 조작과 UI 관련 기능을 담당합니다.
 */
class FigureModalUI {
  constructor() {
    this.init();
  }

  /**
   * UI 요소 초기화
   */
  init() {
    // 모달 요소
    this.figureAddModal = document.getElementById('figureAddModal');
    this.figureModalContent = document.getElementById('figureModalContent');
    this.modalTitle = document.getElementById('modalTitle');

    // 단계 표시기 요소
    this.stepIndicators = document.querySelectorAll('.step-indicator');
    this.stepProgress = document.querySelectorAll('.step-progress');

    // 모달 스텝
    this.stepCategory = document.getElementById('stepCategory');
    this.stepName = document.getElementById('stepName');
    this.stepCandidates = document.getElementById('stepCandidates');
    this.stepConfirm = document.getElementById('stepConfirm');
    this.stepFailure = document.getElementById('stepFailure');

    // 카테고리 관련 요소
    this.categoryLoading = document.getElementById('categoryLoading');
    this.categoryError = document.getElementById('categoryError');
    this.categoryGrid = document.getElementById('categoryGrid');
    this.retryCategoryBtn = document.getElementById('retryCategoryBtn');

    // 후보 인물 관련 요소
    this.candidatesLoading = document.getElementById('candidatesLoading');
    this.noCandidatesFound = document.getElementById('noCandidatesFound');
    this.candidatesList = document.getElementById('candidatesList');
    this.candidateCardTemplate = document.getElementById(
        'candidateCardTemplate');
    this.returnToNameInputBtn = document.getElementById('returnToNameInputBtn');

    // 스텝별 버튼 그룹
    this.stepCategoryButtons = document.getElementById('stepCategoryButtons');
    this.stepNameButtons = document.getElementById('stepNameButtons');
    this.stepCandidatesButtons = document.getElementById(
        'stepCandidatesButtons');
    this.stepConfirmButtons = document.getElementById('stepConfirmButtons');
    this.stepFailureButtons = document.getElementById('stepFailureButtons');

    // 폼 요소
    this.figureForm = document.getElementById('figureForm');
    this.formFigureName = document.getElementById('formFigureName');
    this.formCategoryId = document.getElementById('formCategoryId');
    this.formImageUrl = document.getElementById('formImageUrl');
    this.formBio = document.getElementById('formBio');

    // 입력 및 표시 요소
    this.selectedCategory = document.getElementById('selectedCategory');
    this.selectedCategoryImage = document.getElementById(
        'selectedCategoryImage');
    this.selectedCategoryDescription = document.getElementById(
        'selectedCategoryDescription');
    this.figureName = document.getElementById('figureName');
    this.figureImage = document.getElementById('figureImage');
    this.figureSummaryName = document.getElementById('figureSummaryName');
    this.figureSummary = document.getElementById('figureSummary');
    this.nameSearchLoading = document.getElementById('nameSearchLoading');

    // 검색어 추천 관련 요소
    this.searchSuggestions = document.getElementById('searchSuggestions');

    // 알림 관련 요소
    this.modalAlertContainer = document.getElementById('modalAlertContainer');
    this.modalAlert = document.getElementById('modalAlert');
    this.alertIcon = document.getElementById('alertIcon');
    this.modalAlertMessage = document.getElementById('modalAlertMessage');

    // 버튼 요소
    this.searchFigureBtn = document.getElementById('searchFigure');
    this.backToCategoriesBtn = document.getElementById('backToCategories');
    this.backToNameInputBtn = document.getElementById('backToNameInput');
    this.manualRegisterBtn = document.getElementById('manualRegister');
    this.submitFigureBtn = document.getElementById('submitFigure');
    this.notThisFigureBtn = document.getElementById('notThisFigure');
    this.contactButton = document.getElementById('contactButton');
    this.closeFailureBtn = document.getElementById('closeFailure');
    this.closeModalBtn = document.getElementById('closeModal');
    this.cancelButton = document.getElementById('cancelButton');

    // 원본 인물 확인 스텝 컨텐츠
    this.originalConfirmContent = null;
  }

  /**
   * 모달 열기
   */
  openModal() {
    this.figureAddModal.classList.remove('hidden');
    setTimeout(() => {
      this.figureModalContent.classList.remove('scale-95', 'opacity-0');
      this.figureModalContent.classList.add('scale-100', 'opacity-100');
    }, 10);
  }

  /**
   * 모달 닫기
   * @param {Function} resetCallback - 모달 리셋 콜백 함수
   */
  closeModal(resetCallback) {
    this.figureModalContent.classList.remove('scale-100', 'opacity-100');
    this.figureModalContent.classList.add('scale-95', 'opacity-0');

    setTimeout(() => {
      this.figureAddModal.classList.add('hidden');
      if (typeof resetCallback === 'function') {
        resetCallback();
      }
    }, 300);
  }

  /**
   * 알림 메시지 표시
   * @param {string} message - 표시할 메시지
   * @param {string} type - 알림 타입 (error, success, info, warning)
   */
  showAlert(message, type = 'error') {
    this.modalAlertMessage.textContent = message;

    // 기존 클래스 제거
    this.modalAlert.className = 'p-4 rounded-md';
    this.alertIcon.className = 'fa-solid mt-0.5 mr-2';

    // 타입에 따른 스타일 적용
    if (type === 'error') {
      this.modalAlert.classList.add('bg-red-100', 'border-l-4',
          'border-red-500',
          'text-red-700', 'dark:bg-red-900/30', 'dark:text-red-300',
          'dark:border-red-500');
      this.alertIcon.classList.add('fa-exclamation-circle', 'text-red-500',
          'dark:text-red-400');
    } else if (type === 'success') {
      this.modalAlert.classList.add('bg-green-100', 'border-l-4',
          'border-green-500',
          'text-green-700', 'dark:bg-green-900/30', 'dark:text-green-300',
          'dark:border-green-500');
      this.alertIcon.classList.add('fa-check-circle', 'text-green-500',
          'dark:text-green-400');
    } else if (type === 'info') {
      this.modalAlert.classList.add('bg-blue-100', 'border-l-4',
          'border-blue-500',
          'text-blue-700', 'dark:bg-blue-900/30', 'dark:text-blue-300',
          'dark:border-blue-500');
      this.alertIcon.classList.add('fa-info-circle', 'text-blue-500',
          'dark:text-blue-400');
    } else if (type === 'warning') {
      this.modalAlert.classList.add('bg-amber-100', 'border-l-4',
          'border-amber-500',
          'text-amber-700', 'dark:bg-amber-900/30', 'dark:text-amber-300',
          'dark:border-amber-500');
      this.alertIcon.classList.add('fa-exclamation-triangle', 'text-amber-500',
          'dark:text-amber-400');
    }

    // 알림 표시
    this.modalAlertContainer.classList.remove('hidden');

    // 3초 후 자동으로 사라짐
    setTimeout(() => {
      this.modalAlertContainer.classList.add('hidden');
    }, 3000);
  }

  /**
   * 단계 표시기 업데이트
   * @param {number} step - 현재 단계
   */
  updateStepIndicators(step) {
    this.stepIndicators.forEach((indicator, index) => {
      const stepNum = index + 1;

      if (stepNum < step) {
        // 이전 단계: 완료됨
        indicator.classList.remove('bg-white', 'bg-opacity-30', 'text-white');
        indicator.classList.add('bg-white', 'text-indigo-600');
        indicator.innerHTML = '<i class="fas fa-check"></i>';

        // 이전 단계의 진행 표시줄 완료
        if (index < this.stepProgress.length) {
          this.stepProgress[index].style.width = '100%';
        }
      } else if (stepNum === step) {
        // 현재 단계: 활성화
        indicator.classList.remove('bg-opacity-30', 'text-white');
        indicator.classList.add('bg-white', 'text-indigo-600');
        indicator.textContent = stepNum;

        // 이전 단계 진행 표시줄 완료
        if (index > 0 && index - 1 < this.stepProgress.length) {
          this.stepProgress[index - 1].style.width = '100%';
        }
      } else {
        // 이후 단계: 비활성화
        indicator.classList.remove('bg-white', 'text-indigo-600');
        indicator.classList.add('bg-white', 'bg-opacity-30', 'text-white');
        indicator.textContent = stepNum;

        // 이후 단계의 진행 표시줄 초기화
        if (index < this.stepProgress.length) {
          this.stepProgress[index].style.width = '0%';
        }
      }
    });
  }

  /**
   * 현재 스텝 변경
   * @param {number} step - 표시할 스텝 번호
   * @param {Object} data - 스텝에 표시할 데이터
   */
  goToStep(step, data = {}) {
    // 모든 스텝 숨기기
    this.stepCategory.classList.add('hidden');
    this.stepName.classList.add('hidden');
    this.stepCandidates.classList.add('hidden');
    this.stepConfirm.classList.add('hidden');
    this.stepFailure.classList.add('hidden');

    // 모든 버튼 그룹 숨기기
    this.stepCategoryButtons.classList.add('hidden');
    this.stepNameButtons.classList.add('hidden');
    this.stepCandidatesButtons.classList.add('hidden');
    this.stepConfirmButtons.classList.add('hidden');
    this.stepFailureButtons.classList.add('hidden');

    // 추천 목록 숨기기
    this.hideSearchSuggestions();

    // 단계 표시기 업데이트
    this.updateStepIndicators(step);

    // 스텝별 타이틀 및 표시 설정
    switch (step) {
      case 1: // 카테고리 선택 스텝
        this.modalTitle.textContent = '인물 추가';
        this.stepCategory.classList.remove('hidden');
        this.stepCategoryButtons.classList.remove('hidden');
        break;

      case 2: // 인물 이름 입력 스텝
        this.modalTitle.textContent = '인물 이름 입력';
        this.stepName.classList.remove('hidden');
        this.stepNameButtons.classList.remove('hidden');

        // 선택된 카테고리 정보 표시
        this.selectedCategory.textContent = data.categoryName || '';
        this.selectedCategoryDescription.textContent = data.categoryDesc || '';

        // 카테고리 이미지 설정
        if (data.categoryImageUrl) {
          this.selectedCategoryImage.src = data.categoryImageUrl;
          this.selectedCategoryImage.classList.remove('hidden');
        } else {
          this.selectedCategoryImage.classList.add('hidden');
        }

        // 이름 입력란에 포커스
        setTimeout(() => this.figureName.focus(), 100);
        break;

      case 3: // 인물 후보 선택 스텝
        this.modalTitle.textContent = '인물 선택';
        this.stepCandidates.classList.remove('hidden');
        this.stepCandidatesButtons.classList.remove('hidden');

        // 로딩 상태 표시
        this.candidatesLoading.classList.remove('hidden');
        this.noCandidatesFound.classList.add('hidden');
        this.candidatesList.classList.add('hidden');
        break;

      case 4: // 인물 확인 스텝
        this.modalTitle.textContent = '인물 확인';
        this.stepConfirm.classList.remove('hidden');
        this.stepConfirmButtons.classList.remove('hidden');

        // 인물 확인 스텝 원본 내용 저장 (처음 한번만)
        if (!this.originalConfirmContent) {
          this.originalConfirmContent = this.stepConfirm.innerHTML;
        }

        // 인물 정보 표시
        if (data.figure) {
          this.figureImage.src = data.figure.imageUrl;
          this.figureSummaryName.textContent = data.figure.name;
          this.figureSummary.textContent = data.figure.bio;

          // 폼 값 설정
          this.formFigureName.value = data.figure.name;
          this.formCategoryId.value = data.categoryId || '';
          this.formImageUrl.value = data.figure.imageUrl;
          this.formBio.value = data.figure.bio;
        }
        break;

      case 5: // 등록 실패 안내 스텝
        this.modalTitle.textContent = '인물 찾기 실패';
        this.stepFailure.classList.remove('hidden');
        this.stepFailureButtons.classList.remove('hidden');
        break;
    }

    return step;
  }

  /**
   * 검색어 추천 목록 표시
   * @param {Array} suggestions - 추천 검색어 배열
   * @param {Array} urls - 추천 검색어 URL 배열
   * @param {Function} onSuggestionSelect - 추천어 선택 시 콜백
   */
  displaySearchSuggestions(suggestions, urls, onSuggestionSelect) {
    // 기존 추천 목록 초기화
    const suggestionsList = this.searchSuggestions.querySelector('ul');
    suggestionsList.innerHTML = '';

    // 새 추천 목록 생성
    suggestions.forEach((suggestion, index) => {
      const li = document.createElement('li');
      li.className = 'px-4 py-2.5 hover:bg-indigo-50 dark:hover:bg-indigo-900 cursor-pointer transition-colors border-b border-gray-100 dark:border-gray-600 last:border-0 flex items-center';

      // 검색 아이콘 추가
      const icon = document.createElement('i');
      icon.className = 'fas fa-search mr-2 text-gray-400 dark:text-gray-500';
      li.appendChild(icon);

      // 텍스트 추가
      const span = document.createElement('span');
      span.textContent = suggestion;
      li.appendChild(span);

      li.setAttribute('data-url', urls[index]);

      // 클릭 이벤트 추가
      li.addEventListener('click', () => {
        this.figureName.value = suggestion;
        this.hideSearchSuggestions();
        if (typeof onSuggestionSelect === 'function') {
          onSuggestionSelect(suggestion, urls[index]);
        }
      });

      suggestionsList.appendChild(li);
    });

    // 추천 목록 위치 조정
    this.positionSuggestions();

    // 추천 목록 표시 (애니메이션)
    this.searchSuggestions.classList.remove('hidden');
    this.searchSuggestions.style.animation = 'suggestionsPopIn 0.2s ease forwards';
  }

  /**
   * 추천 목록 위치 조정
   */
  positionSuggestions() {
    if (window.innerWidth < 640) { // 모바일 화면에서는 모달 본문 내부에 표시
      this.searchSuggestions.style.position = 'relative';
      this.searchSuggestions.style.top = '0';
      this.searchSuggestions.style.left = '0';
      this.searchSuggestions.style.width = '100%';
    } else {
      // PC 화면에서는 적절한 z-index로 푸터 위에 표시
      this.searchSuggestions.style.position = 'relative';
      this.searchSuggestions.style.top = '0';
      this.searchSuggestions.style.left = '0';
      this.searchSuggestions.style.width = '100%';
      this.searchSuggestions.style.zIndex = '30';
    }
  }

  /**
   * 검색어 추천 목록 숨기기
   */
  hideSearchSuggestions() {
    this.searchSuggestions.classList.add('hidden');
  }

  /**
   * 인물 후보 목록 표시
   * @param {Array} candidates - 후보 인물 배열
   * @param {Function} onCandidateSelect - 후보 인물 선택 시 콜백
   */
  displayFigureCandidates(candidates, onCandidateSelect) {
    // 로딩 상태 및 비어있음 메시지 숨기기
    this.candidatesLoading.classList.add('hidden');
    this.noCandidatesFound.classList.add('hidden');

    // 후보 목록 초기화
    this.candidatesList.innerHTML = '';

    if (!candidates || candidates.length === 0) {
      // 후보가 없으면 메시지 표시
      this.noCandidatesFound.classList.remove('hidden');
      return;
    }

    // 각 후보 카드 생성
    candidates.forEach(candidate => {
      // 템플릿 복제
      const candidateCard = this.candidateCardTemplate.content.cloneNode(true);

      // 데이터 채우기
      candidateCard.querySelector('.candidate-image').src = candidate.imageUrl;
      candidateCard.querySelector('.candidate-image').alt = candidate.name;
      candidateCard.querySelector(
          '.candidate-name').textContent = candidate.name;
      candidateCard.querySelector('.candidate-bio').textContent = candidate.bio;

      // 카드 클릭 이벤트 - 해당 인물 선택하여 다음 단계로
      const cardElement = candidateCard.querySelector('.candidate-card');
      cardElement.addEventListener('click', () => {
        if (typeof onCandidateSelect === 'function') {
          onCandidateSelect(candidate);
        }
      });

      // 목록에 추가
      this.candidatesList.appendChild(candidateCard);
    });

    // 후보 목록 표시
    this.candidatesList.classList.remove('hidden');
  }

  /**
   * 카테고리 로딩 상태 표시
   * @param {boolean} isLoading - 로딩 중 여부
   * @param {boolean} hasError - 오류 발생 여부
   */
  setCategoryLoadingState(isLoading, hasError = false) {
    if (isLoading) {
      this.categoryLoading.classList.remove('hidden');
      this.categoryGrid.classList.add('hidden');
      this.categoryError.classList.add('hidden');
    } else if (hasError) {
      this.categoryLoading.classList.add('hidden');
      this.categoryGrid.classList.add('hidden');
      this.categoryError.classList.remove('hidden');
    } else {
      this.categoryLoading.classList.add('hidden');
      this.categoryGrid.classList.remove('hidden');
      this.categoryError.classList.add('hidden');
    }
  }

  /**
   * 이름 검색 로딩 상태 표시
   * @param {boolean} isLoading - 로딩 중 여부
   */
  setNameSearchLoading(isLoading) {
    if (isLoading) {
      this.nameSearchLoading.classList.remove('hidden');
      this.searchFigureBtn.disabled = true;
      this.backToCategoriesBtn.disabled = true;
    } else {
      this.nameSearchLoading.classList.add('hidden');
      this.searchFigureBtn.disabled = false;
      this.backToCategoriesBtn.disabled = false;
    }
  }

  /**
   * 카테고리 버튼 생성
   * @param {Array} categories - 카테고리 배열
   * @param {Function} onCategorySelect - 카테고리 선택 콜백
   */
  createCategoryButtons(categories, onCategorySelect) {
    // 카테고리 그리드 비우기
    this.categoryGrid.innerHTML = '';

    // 카테고리 버튼 생성
    categories.forEach(category => {
      const button = document.createElement('button');
      button.className = 'category-button flex flex-col items-center justify-center p-5 border border-gray-200 dark:border-gray-700 rounded-xl transition-all hover:bg-indigo-50 dark:hover:bg-indigo-900/50 hover:shadow-md transform hover:scale-105 duration-300 group overflow-hidden';
      button.dataset.categoryId = category.id;
      button.dataset.categoryName = category.name;
      button.dataset.categoryDescription = category.description || '';
      button.dataset.categoryImage = category.imageUrl || '';

      const imageContainer = document.createElement('div');
      imageContainer.className = 'w-16 h-16 rounded-full bg-indigo-100 dark:bg-indigo-900/50 mb-3 flex items-center justify-center group-hover:bg-indigo-200 dark:group-hover:bg-indigo-800 transition-colors overflow-hidden';

      if (category.imageUrl) {
        const img = document.createElement('img');
        img.className = 'w-full h-full object-cover';
        img.src = category.imageUrl;
        img.alt = '카테고리 이미지';
        imageContainer.appendChild(img);
      } else {
        const icon = document.createElement('i');
        icon.className = 'fas fa-user text-2xl text-indigo-500 dark:text-indigo-400';
        imageContainer.appendChild(icon);
      }

      const name = document.createElement('span');
      name.className = 'text-base font-medium text-gray-800 dark:text-gray-200 group-hover:text-indigo-600 dark:group-hover:text-indigo-400 transition-colors';
      name.textContent = category.name;

      const description = document.createElement('p');
      description.className = 'text-xs text-gray-500 dark:text-gray-400 mt-1 line-clamp-2 text-center';
      description.textContent = category.description || '';

      button.appendChild(imageContainer);
      button.appendChild(name);
      button.appendChild(description);

      // 카테고리 선택 이벤트
      button.addEventListener('click', () => {
        if (typeof onCategorySelect === 'function') {
          onCategorySelect(category);
        }
      });

      this.categoryGrid.appendChild(button);
    });
  }

  /**
   * 폼 제출하기
   */
  submitForm() {
    this.figureForm.submit();
  }

  /**
   * 중복 인물 존재 UI 표시
   * @param {Object} figureData - 중복 인물 데이터
   * @param {Function} onMoveToPage - 인물 페이지 이동 콜백
   * @param {Function} onCancel - 취소 콜백
   */
  showDuplicateFigureUI(figureData, onMoveToPage, onCancel) {
    // 원본 내용이 백업되어 있지 않으면 백업
    if (!this.originalConfirmContent) {
      this.originalConfirmContent = this.stepConfirm.innerHTML;
    }

    // 기존 버튼 숨김
    this.stepConfirmButtons.classList.add('hidden');

    // 인물 중복 안내 UI로 변경
    this.stepConfirm.innerHTML = `
      <div class="text-center py-4 flex flex-col items-center">
        <div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-blue-100 dark:bg-blue-900 mb-5">
          <i class="fas fa-info-circle text-4xl text-blue-500 dark:text-blue-400"></i>
        </div>
        <h3 class="text-xl font-semibold text-gray-800 dark:text-gray-100 mb-3">
          ${figureData.categoryName}에 ${figureData.name}(이)가<br>이미 등록되어 있어요 :)
        </h3>
        <p class="text-gray-600 dark:text-gray-300 mb-6 max-w-md">
          페이지로 바로 이동할까요?
        </p>
        
        <div class="flex flex-col sm:flex-row gap-3 mt-2">
          <button id="moveToFigurePageBtn" class="inline-flex justify-center items-center px-5 py-2.5 border border-transparent rounded-lg text-white bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-all hover:scale-105">
            <i class="fas fa-external-link-alt mr-2"></i>
            인물 페이지로 이동
          </button>
          <button id="stayInModalBtn" class="inline-flex justify-center items-center px-5 py-2.5 border border-gray-300 dark:border-gray-600 rounded-lg text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 transition-colors">
            <i class="fas fa-times mr-2"></i>
            취소
          </button>
        </div>
      </div>
    `;

    // 인물 페이지로 이동 버튼 이벤트
    document.getElementById('moveToFigurePageBtn').addEventListener('click',
        () => {
          if (typeof onMoveToPage === 'function') {
            onMoveToPage(figureData);
          }
        });

    // 취소 버튼 이벤트
    document.getElementById('stayInModalBtn').addEventListener('click', () => {
      // 원래 인물 확인 UI로 복원
      this.stepConfirm.innerHTML = this.originalConfirmContent;
      this.stepConfirmButtons.classList.remove('hidden');

      if (typeof onCancel === 'function') {
        onCancel();
      }
    });
  }

  /**
   * 제출 버튼 로딩 상태 설정
   * @param {boolean} isLoading - 로딩 중 여부
   * @param {string} originalText - 원래 버튼 텍스트
   * @returns {string} 이전 버튼 텍스트
   */
  setSubmitButtonLoading(isLoading, originalText) {
    if (isLoading) {
      const currentText = this.submitFigureBtn.innerHTML;
      this.submitFigureBtn.disabled = true;
      this.notThisFigureBtn.disabled = true;
      this.submitFigureBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>확인 중...';
      return currentText;
    } else {
      this.submitFigureBtn.disabled = false;
      this.notThisFigureBtn.disabled = false;
      if (originalText) {
        this.submitFigureBtn.innerHTML = originalText;
      }
      return null;
    }
  }

  /**
   * 모달 리셋
   */
  resetModal() {
    // 입력값 초기화
    this.figureName.value = '';

    // 알림 숨기기
    this.modalAlertContainer.classList.add('hidden');

    // 로딩 상태 숨기기
    this.nameSearchLoading.classList.add('hidden');
    this.candidatesLoading.classList.add('hidden');

    // 카테고리 관련 요소 초기화
    this.categoryLoading.classList.add('hidden');
    this.categoryError.classList.add('hidden');
    this.categoryGrid.classList.remove('hidden');

    // 후보 관련 요소 초기화
    this.noCandidatesFound.classList.add('hidden');
    this.candidatesList.classList.add('hidden');
    this.candidatesList.innerHTML = '';

    // 검색 추천 목록 숨기기
    this.hideSearchSuggestions();

    // 만약 인물 확인 스텝이 변경되었다면 원래대로 복원
    if (this.originalConfirmContent) {
      this.stepConfirm.innerHTML = this.originalConfirmContent;
      this.originalConfirmContent = null;
    }

    // 버튼 초기화
    this.searchFigureBtn.disabled = true;
  }

  /**
   * 카테고리 버튼 이벤트 바인딩
   * @param {Function} onCategorySelect - 카테고리 선택 콜백
   */
  bindCategoryButtonEvents(onCategorySelect) {
    document.querySelectorAll('.category-button').forEach(button => {
      // 이미 이벤트가 바인딩되어 있는지 확인 (중복 바인딩 방지)
      if (!button.dataset.eventBound) {
        button.addEventListener('click', () => {
          const category = {
            id: button.dataset.categoryId,
            name: button.dataset.categoryName,
            imageUrl: button.dataset.categoryImage || '',
            description: button.dataset.categoryDescription || ''
          };

          if (typeof onCategorySelect === 'function') {
            onCategorySelect(category);
          }
        });

        // 이벤트 바인딩 표시
        button.dataset.eventBound = 'true';
      }
    });
  }

  /**
   * 검색 버튼 비활성화 여부 설정
   * @param {boolean} disabled - 비활성화 여부
   */
  setSearchButtonDisabled(disabled) {
    this.searchFigureBtn.disabled = disabled;
  }

  /**
   * 윈도우 리사이즈 이벤트 리스너 설정
   */
  setupResizeListener() {
    window.addEventListener('resize', () => {
      if (!this.searchSuggestions.classList.contains('hidden')) {
        this.positionSuggestions();
      }
    });
  }

  /**
   * Enter 키 이벤트 리스너 설정
   * @param {Function} searchCallback - 검색 콜백 함수
   */
  setupEnterKeyListener(searchCallback) {
    this.figureName.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' && !this.searchFigureBtn.disabled) {
        e.preventDefault();
        if (typeof searchCallback === 'function') {
          searchCallback();
        }
      }
    });
  }

  /**
   * 외부 클릭으로 추천 목록 숨기기 이벤트 설정
   */
  setupOutsideClickListener() {
    document.addEventListener('click', (e) => {
      if (!this.searchSuggestions.contains(e.target) && e.target
          !== this.figureName) {
        this.hideSearchSuggestions();
      }
    });
  }

  /**
   * 모달 닫기 이벤트 설정
   * @param {Function} onClose - 닫기 콜백 함수
   */
  setupCloseListeners(onClose) {
    // 닫기 버튼 클릭
    this.closeModalBtn.addEventListener('click', () => {
      if (typeof onClose === 'function') {
        onClose();
      }
    });

    // 취소 버튼 클릭
    this.cancelButton.addEventListener('click', () => {
      if (typeof onClose === 'function') {
        onClose();
      }
    });

    // 실패 화면 닫기 버튼 클릭
    this.closeFailureBtn.addEventListener('click', () => {
      if (typeof onClose === 'function') {
        onClose();
      }
    });

    // 모달 외부 클릭
    this.figureAddModal.addEventListener('click', (e) => {
      if (e.target === this.figureAddModal) {
        if (typeof onClose === 'function') {
          onClose();
        }
      }
    });

    // ESC 키 누르면 모달 닫기
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && !this.figureAddModal.classList.contains(
          'hidden')) {
        if (typeof onClose === 'function') {
          onClose();
        }
      }
    });
  }

  /**
   * 카테고리 재시도 버튼 이벤트 설정
   * @param {Function} retryCallback - 재시도 콜백 함수
   */
  setupRetryButtonListener(retryCallback) {
    this.retryCategoryBtn.addEventListener('click', () => {
      if (typeof retryCallback === 'function') {
        retryCallback();
      }
    });
  }

  /**
   * 이전 버튼 이벤트 설정
   * @param {Function} backToCategories - 카테고리로 돌아가기 콜백
   * @param {Function} backToNameInput - 이름 입력으로 돌아가기 콜백
   */
  setupBackButtonListeners(backToCategories, backToNameInput) {
    this.backToCategoriesBtn.addEventListener('click', () => {
      if (typeof backToCategories === 'function') {
        backToCategories();
      }
    });

    this.backToNameInputBtn.addEventListener('click', () => {
      if (typeof backToNameInput === 'function') {
        backToNameInput();
      }
    });

    this.returnToNameInputBtn.addEventListener('click', () => {
      if (typeof backToNameInput === 'function') {
        backToNameInput();
      }
    });
  }

  /**
   * 검색 버튼 이벤트 설정
   * @param {Function} searchCallback - 검색 콜백 함수
   */
  setupSearchButtonListener(searchCallback) {
    this.searchFigureBtn.addEventListener('click', () => {
      if (typeof searchCallback === 'function') {
        searchCallback();
      }
    });
  }

  /**
   * 인물 선택 관련 버튼 이벤트 설정
   * @param {Function} notThisFigure - 다른 인물 선택 콜백
   * @param {Function} submitFigure - 인물 제출 콜백
   * @param {Function} manualRegister - 수동 등록 콜백
   */
  setupFigureSelectionListeners(notThisFigure, submitFigure, manualRegister) {
    this.notThisFigureBtn.addEventListener('click', () => {
      if (typeof notThisFigure === 'function') {
        notThisFigure();
      }
    });

    this.submitFigureBtn.addEventListener('click', () => {
      if (typeof submitFigure === 'function') {
        submitFigure();
      }
    });

    this.manualRegisterBtn.addEventListener('click', () => {
      if (typeof manualRegister === 'function') {
        manualRegister();
      }
    });
  }

  /**
   * 채널톡 문의 버튼 이벤트 설정
   * @param {Function} contactCallback - 문의하기 콜백 함수
   */
  setupContactButtonListener(contactCallback) {
    this.contactButton.addEventListener('click', () => {
      if (typeof contactCallback === 'function') {
        contactCallback();
      }
    });
  }
}

// 전역으로 노출
window.FigureModalUI = FigureModalUI;