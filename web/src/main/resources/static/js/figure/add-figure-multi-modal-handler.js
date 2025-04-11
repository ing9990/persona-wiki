/**
 * 인물 추가 다단계 모달 핸들러
 * API 호출 및 로직 처리를 담당합니다.
 */
class FigureModalHandler {
  /**
   * 인물 추가 모달 핸들러 생성자
   * @param {FigureModalUI} ui - UI 컴포넌트 인스턴스
   */
  constructor(ui) {
    this.ui = ui;
    this.init();
  }

  /**
   * 초기화 및 이벤트 바인딩
   */
  init() {
    // 상태 관리
    this.currentStep = 1;
    this.selectedCategoryId = '';
    this.selectedCategoryName = '';
    this.selectedCategoryImageUrl = '';
    this.selectedCategoryDesc = '';
    this.searchTimeout = null;
    this.figureCandidates = [];
    this.selectedFigureData = null;

    // 이름 유효성 검사 정규식
    this.nameRegex = /^([가-힣]{1,20}|[a-zA-Z]{1,20})$/;

    // 이벤트 리스너 설정
    this.setupEventListeners();

    console.info('[FigureModalHandler] 초기화 완료');
  }

  /**
   * 이벤트 리스너 설정
   */
  setupEventListeners() {
    // 기본 UI 이벤트 설정
    this.ui.setupEnterKeyListener(() => this.searchFigureHandler());
    this.ui.setupOutsideClickListener();
    this.ui.setupResizeListener();

    // 검색/이동 버튼 이벤트
    this.ui.setupSearchButtonListener(() => this.searchFigureHandler());
    this.ui.setupBackButtonListeners(
        () => this.goToStep(1), // 카테고리로 돌아가기
        () => this.goToStep(2)  // 이름 입력으로 돌아가기
    );

    // 인물 선택 관련 버튼 이벤트
    this.ui.setupFigureSelectionListeners(
        () => this.goToStep(5), // 다른 인물 선택
        () => this.submitFigure(), // 인물 제출
        () => this.goToStep(5)  // 수동 등록
    );

    // 카테고리 재시도 버튼 이벤트
    this.ui.setupRetryButtonListener(() => this.fetchCategories());

    // 모달 닫기 이벤트
    this.ui.setupCloseListeners(() => this.closeModal());

    // 채널톡 문의 버튼 이벤트
    this.ui.setupContactButtonListener(() => this.openChannelTalk());

    // 입력 필드 변경 이벤트 (실시간 검색어 추천)
    this.ui.figureName.addEventListener('input', () => {
      const query = this.ui.figureName.value.trim();

      // 입력값 없을 때 검색 버튼 비활성화
      this.ui.setSearchButtonDisabled(!query);

      // 디바운싱 적용 (300ms)
      clearTimeout(this.searchTimeout);
      this.searchTimeout = setTimeout(() => {
        this.fetchWikipediaSuggestions(query);
      }, 300);
    });
  }

  /**
   * 모달 열기
   */
  openModal() {
    // UI 리셋
    this.resetModal();

    // 모달 표시
    this.ui.openModal();

    // 카테고리 데이터 로딩
    this.fetchCategories();
  }

  /**
   * 모달 닫기
   */
  closeModal() {
    this.ui.closeModal(() => this.resetModal());
  }

  /**
   * 모달 상태 리셋
   */
  resetModal() {
    // UI 리셋
    this.ui.resetModal();

    // 상태 리셋
    this.currentStep = 1;
    this.selectedCategoryId = '';
    this.selectedCategoryName = '';
    this.selectedCategoryImageUrl = '';
    this.selectedCategoryDesc = '';
    this.figureCandidates = [];
    this.selectedFigureData = null;

    // 첫 번째 단계로 이동
    this.currentStep = this.ui.goToStep(1);
  }

  /**
   * 단계 이동
   * @param {number} step - 이동할 스텝 번호
   */
  goToStep(step) {
    // 단계별 데이터 준비
    let stepData = {};

    switch (step) {
      case 2: // 인물 이름 입력 스텝
        stepData = {
          categoryId: this.selectedCategoryId,
          categoryName: this.selectedCategoryName,
          categoryImageUrl: this.selectedCategoryImageUrl,
          categoryDesc: this.selectedCategoryDesc
        };
        break;

      case 4: // 인물 확인 스텝
        stepData = {
          categoryId: this.selectedCategoryId,
          figure: this.selectedFigureData
        };
        break;
    }

    // UI에 단계 변경 요청
    this.currentStep = this.ui.goToStep(step, stepData);

    // 단계별 추가 작업
    if (step === 3) {
      // 인물 후보 목록 표시 (약간의 지연으로 애니메이션 효과)
      setTimeout(() => {
        this.displayFigureCandidates();
      }, 500);
    }
  }

  /**
   * 카테고리 목록 가져오기
   */
  async fetchCategories() {
    try {
      // 로딩 상태 표시
      this.ui.setCategoryLoadingState(true);

      // 이미 서버에서 Thymeleaf로 주입된 데이터가 있는지 확인
      const existingCategories = document.querySelectorAll('.category-button');

      if (existingCategories.length > 0) {
        // 이미 데이터가 주입되어 있으면 바로 표시
        this.ui.setCategoryLoadingState(false);

        // 카테고리 버튼에 이벤트 바인딩
        this.ui.bindCategoryButtonEvents(
            category => this.onCategorySelect(category));
        return;
      }

      // 없으면 API 호출
      const response = await fetch('/api/v1/categories');

      if (!response.ok) {
        throw new Error('카테고리 로딩 실패');
      }

      const categories = await response.json();

      // 카테고리 버튼 생성 및 이벤트 바인딩
      this.ui.createCategoryButtons(categories,
          category => this.onCategorySelect(category));

      // 로딩 상태 숨기기
      this.ui.setCategoryLoadingState(false);

      // 성공 토스트 메시지 (선택적)
      if (window.toastManager) {
        window.toastManager.success('카테고리를 불러왔습니다');
      }

    } catch (error) {
      console.error('카테고리 로딩 오류:', error);
      this.ui.setCategoryLoadingState(false, true);

      // 에러 토스트 메시지 (선택적)
      if (window.toastManager) {
        window.toastManager.error('카테고리를 불러오는데 실패했습니다');
      }
    }
  }

  /**
   * 카테고리 선택 이벤트 핸들러
   * @param {Object} category - 선택된 카테고리 정보
   */
  onCategorySelect(category) {
    this.selectedCategoryId = category.id;
    this.selectedCategoryName = category.name;
    this.selectedCategoryImageUrl = category.imageUrl || '';
    this.selectedCategoryDesc = category.description || '';

    this.goToStep(2);
  }

  /**
   * 위키피디아 검색 추천 API 호출
   * @param {string} query - 검색어
   */
  async fetchWikipediaSuggestions(query) {
    if (!query || query.length < 1) {
      this.ui.hideSearchSuggestions();
      return;
    }

    try {
      const response = await fetch(
          `https://ko.wikipedia.org/w/api.php?action=opensearch&search=${encodeURIComponent(
              query)}&limit=5&namespace=0&format=json&origin=*`);
      if (!response.ok) {
        throw new Error('API 응답 오류');
      }

      const data = await response.json();

      // 결과가 있으면 추천 목록 표시
      if (data && data[1] && data[1].length > 0) {
        this.ui.displaySearchSuggestions(
            data[1],
            data[3],
            (suggestion, url) => this.searchWikipediaInfo(suggestion, url)
        );
      } else {
        this.ui.hideSearchSuggestions();
      }
    } catch (error) {
      console.error('검색어 추천 API 오류:', error);
      this.ui.hideSearchSuggestions();
    }
  }

  /**
   * 인물 후보 검색
   */
  async searchFigureCandidates() {
    const name = this.ui.figureName.value.trim();

    // 로딩 상태 표시
    this.ui.setNameSearchLoading(true);

    try {
      // 위키피디아 검색 API 호출
      const searchResponse = await fetch(
          `https://ko.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(
              name)}&format=json&origin=*`);

      if (!searchResponse.ok) {
        throw new Error('위키피디아 검색 API 오류');
      }

      const searchData = await searchResponse.json();
      const searchResults = searchData.query.search;

      // 검색 결과 없음
      if (!searchResults || searchResults.length === 0) {
        // 로딩 상태 숨기기
        this.ui.setNameSearchLoading(false);

        // 실패 스텝으로 이동
        this.goToStep(5);
        return;
      }

      // 상위 3개 결과만 사용
      const topResults = searchResults.slice(0, 3);

      // 각 결과에 대해 요약 정보 가져오기
      const candidatesPromises = topResults.map(async (result) => {
        try {
          const summaryResponse = await fetch(
              `https://ko.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(
                  result.title)}`);

          if (!summaryResponse.ok) {
            return null;
          }

          const summaryData = await summaryResponse.json();

          return {
            name: summaryData.title,
            imageUrl: summaryData.originalimage?.source
                || '/assets/images/no-image.png',
            bio: summaryData.extract || result.snippet.replace(
                /<\/?span[^>]*>/g, '')
          };
        } catch (e) {
          console.error('인물 요약 정보 로딩 오류:', e);
          return null;
        }
      });

      // 모든 후보 정보 수집
      const candidatesResults = await Promise.all(candidatesPromises);
      this.figureCandidates = candidatesResults.filter(c => c !== null);

      // 로딩 상태 숨기기
      this.ui.setNameSearchLoading(false);

      // 후보가 있으면 스텝 3으로, 없으면 스텝 5로
      if (this.figureCandidates.length > 0) {
        this.goToStep(3);
      } else {
        this.goToStep(5);
      }
    } catch (error) {
      console.error('인물 검색 오류:', error);

      // 로딩 상태 숨기기
      this.ui.setNameSearchLoading(false);

      // 오류 메시지 표시
      this.ui.showAlert('인물 검색 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', 'error');

      // 토스트 메시지 (선택적)
      if (window.toastManager) {
        window.toastManager.error('인물 검색 중 오류가 발생했습니다');
      }
    }
  }

  /**
   * 인물 후보 목록 표시
   */
  displayFigureCandidates() {
    this.ui.displayFigureCandidates(
        this.figureCandidates,
        candidate => this.onCandidateSelect(candidate)
    );
  }

  /**
   * 인물 후보 선택 이벤트 핸들러
   * @param {Object} candidate - 선택된 인물 후보
   */
  onCandidateSelect(candidate) {
    this.selectedFigureData = candidate;
    this.goToStep(4);
  }

  /**
   * 위키피디아 정보 검색 (단일 인물)
   * @param {string} name - 인물 이름
   * @param {string} url - 인물 위키피디아 URL
   */
  async searchWikipediaInfo(name, url) {
    // 로딩 상태 표시
    this.ui.setNameSearchLoading(true);

    try {
      // 위키피디아 인물 검색 API 호출
      const response = await fetch(
          `/api/v1/search-images/wikipedia?figureName=${encodeURIComponent(
              name)}`);
      const data = await response.json();

      // 로딩 상태 숨기기
      this.ui.setNameSearchLoading(false);

      // 결과 확인 및 처리
      if (response.ok && data && data.imageUrl && data.summary) {
        // 선택된 인물 정보 저장
        this.selectedFigureData = {
          name: name,
          imageUrl: data.imageUrl,
          bio: data.summary
        };

        // 다음 단계로 이동
        this.goToStep(4);
      } else {
        // 실패 단계로 이동
        this.goToStep(5);
      }
    } catch (error) {
      console.error('인물 검색 오류:', error);

      // 로딩 상태 숨기기
      this.ui.setNameSearchLoading(false);

      // 오류 메시지 표시
      this.ui.showAlert('인물 검색 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', 'error');

      // 토스트 메시지 (선택적)
      if (window.toastManager) {
        window.toastManager.error('인물 검색 중 오류가 발생했습니다');
      }
    }
  }

  /**
   * 인물 이름 검색 핸들러
   */
  searchFigureHandler() {
    const name = this.ui.figureName.value.trim();

    // 유효성 검사: 빈 값 체크
    if (!name) {
      this.ui.showAlert('인물 이름을 입력해주세요', 'error');
      this.ui.figureName.focus();
      return;
    }

    // 유효성 검사: 정규식 패턴 체크 (한글 또는 영문 1~20자)
    if (!this.nameRegex.test(name)) {
      this.ui.showAlert('이름은 한글 또는 영어만 사용할 수 있으며, 1~20자 이내여야 합니다.', 'error');
      this.ui.figureName.focus();
      return;
    }

    // 인물 후보 검색
    this.searchFigureCandidates();
  }

  /**
   * 채널톡 열기
   */
  openChannelTalk() {
    const name = this.ui.figureName.value.trim();

    // 채널톡 열기 (실제 구현시 채널톡 API 연동 필요)
    if (window.ChannelIO) {
      window.ChannelIO('openChat', {
        // 채널톡에 전달할 데이터
        profile: {
          name: name,
          mobileNumber: ''
        },
        // 초기 메시지
        message: `인물 등록 요청: ${name} (${this.selectedCategoryName})`
      });

      // 토스트 메시지 (선택적)
      if (window.toastManager) {
        window.toastManager.info('채널톡으로 연결합니다');
      }
    } else {
      // 채널톡이 없는 경우 새 창에서 이메일로 연결
      window.open('mailto:contact@국민사형투표.com?subject=인물 등록 요청&body=등록 요청 인물: '
          + name);

      // 토스트 메시지 (선택적)
      if (window.toastManager) {
        window.toastManager.info('이메일 클라이언트로 연결합니다');
      }
    }
  }

  /**
   * 인물 등록 폼 제출
   */
  /**
   * 인물 등록 폼 제출
   */
  async submitFigure() {
    // 최종 유효성 검사
    if (!this.ui.formFigureName.value || !this.ui.formCategoryId.value ||
        !this.ui.formImageUrl.value || !this.ui.formBio.value) {
      this.ui.showAlert('필수 정보가 누락되었습니다. 다시 시도해주세요.', 'error');

      // 토스트 메시지 (선택적)
      if (window.toastManager) {
        window.toastManager.error('필수 정보가 누락되었습니다');
      }
      return;
    }

    try {
      // 버튼 로딩 상태 설정
      const originalBtnText = this.ui.setSubmitButtonLoading(true);

      // 인물 중복 검사 API 호출
      const response = await fetch(
          `/api/search/present-validation?categoryId=${encodeURIComponent(
              this.ui.formCategoryId.value)}&slug=${window.makeSlug(
              this.ui.formFigureName.value)}`);

      // 버튼 원래대로 복원
      this.ui.setSubmitButtonLoading(false, originalBtnText);

      // 응답 코드가 200이면 인물이 이미 존재함
      if (response.ok) {
        const figureData = await response.json();

        // 중복 인물 UI 표시
        this.ui.showDuplicateFigureUI(
            figureData,
            // 페이지 이동 콜백
            (data) => {
              window.location.href = `/${data.categoryId}/@${data.name}`;
            },
            // 취소 콜백
            () => {
              // 아무 작업 없음 (UI에서 처리)
            }
        );

        // 토스트 메시지 (선택적)
        if (window.toastManager) {
          window.toastManager.info(`${figureData.name}(이)가 이미 등록되어 있습니다`);
        }

      } else if (response.status === 404) {
        // 인물이 존재하지 않으면 폼 제출
        this.ui.setSubmitButtonLoading(true,
            '<i class="fas fa-spinner fa-spin mr-2"></i>등록 중...');

        // 토스트 메시지 (선택적)
        if (window.toastManager) {
          window.toastManager.success(
              `${this.ui.formFigureName.value}(을)를 등록합니다`);
        }

        // 폼 제출
        this.ui.submitForm();
      } else {
        // 기타 오류 발생 시
        this.ui.showAlert('인물 정보 확인 중 오류가 발생했습니다. 다시 시도해주세요.', 'error');

        // 토스트 메시지 (선택적)
        if (window.toastManager) {
          window.toastManager.error('인물 정보 확인 중 오류가 발생했습니다');
        }
      }
    } catch (error) {
      console.error('인물 중복 검사 오류:', error);
      this.ui.showAlert('인물 정보 확인 중 오류가 발생했습니다. 다시 시도해주세요.', 'error');
      this.ui.setSubmitButtonLoading(false,
          originalBtnText || '<i class="fas fa-check mr-2"></i>등록');

      // 토스트 메시지 (선택적)
      if (window.toastManager) {
        window.toastManager.error('인물 중복 검사 중 오류가 발생했습니다');
      }
    }
  }
}

// 전역으로 노출
window.FigureModalHandler = FigureModalHandler;