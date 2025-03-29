/**
 * 검색 핸들러
 * 검색 제안 및 자동완성 기능을 제공합니다.
 */
function SearchHandler() {
  return {
    searchQuery: '',
    suggestions: [],
    showSuggestions: false,
    selectedIndex: -1,
    searchTimeout: null,

    // 검색어 입력 시 추천 목록 업데이트
    updateSuggestions() {
      clearTimeout(this.searchTimeout);

      if (this.searchQuery.trim().length === 0) {
        this.suggestions = [];
        this.showSuggestions = false;
        return;
      }

      // 타이핑 멈춘 후 200ms 후에 검색 실행 (타이핑 중에는 API 호출 안함)
      this.searchTimeout = setTimeout(() => {
        this.fetchSuggestions();
      }, 200);
    },

    // API 호출하여 검색 추천 목록 가져오기
    async fetchSuggestions() {
      try {
        const response = await fetch(
            `/api/search/suggestions?query=${encodeURIComponent(
                this.searchQuery)}`
        );

        if (response.ok) {
          const data = await response.json();
          this.suggestions = data;
          this.showSuggestions = data.length > 0;
          this.selectedIndex = -1;
        }
      } catch (error) {
        console.error('검색 추천 목록 가져오기 실패:', error);
        this.suggestions = [];
        this.showSuggestions = false;
      }
    },

    // 추천 목록에서 항목 선택
    selectSuggestion(suggestion) {
      // 인물 상세 페이지로 이동
      window.location.href = `/${suggestion.categoryId}/@${suggestion.name}`;
    },

    // 검색 폼 제출
    submitSearch(e) {
      if (this.selectedIndex >= 0 && this.suggestions[this.selectedIndex]) {
        e.preventDefault();
        this.selectSuggestion(this.suggestions[this.selectedIndex]);
      }
    },

    // 키보드 이벤트 처리 (화살표 키로 목록 이동)
    handleKeydown(e) {
      if (!this.showSuggestions) {
        return;
      }

      if (e.key === 'ArrowDown') {
        e.preventDefault();
        this.selectedIndex = Math.min(this.selectedIndex + 1,
            this.suggestions.length - 1);
      } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        this.selectedIndex = Math.max(this.selectedIndex - 1, -1);
      } else if (e.key === 'Enter' && this.selectedIndex >= 0) {
        e.preventDefault();
        this.selectSuggestion(this.suggestions[this.selectedIndex]);
      } else if (e.key === 'Escape') {
        this.showSuggestions = false;
      }
    }
  };
}

// 전역 범위에서 함수 노출
window.SearchHandler = SearchHandler;