document.addEventListener('DOMContentLoaded', function () {
  // 모달 요소들
  const profileImageModal = document.getElementById('profileImageModal');
  const editNicknameModal = document.getElementById('editNicknameModal');
  const withdrawModal = document.getElementById('withdrawModal');
  const editBioModal = document.getElementById('editBioModal');

  // 모달 트리거 버튼들
  const profileImageTrigger = document.getElementById('profileImageTrigger');
  const editNicknameTrigger = document.getElementById('editNicknameTrigger');
  const withdrawTrigger = document.getElementById('withdrawTrigger');
  const editBioTrigger = document.getElementById('editBioTrigger');

  // 모달 닫기 버튼들
  const closeButtons = document.querySelectorAll('.modal-close');

  // 레벨별 별명
  const levelTitles = [
    "병아리",     // 0레벨
    "견습생",     // 1레벨
    "관찰자",     // 2레벨
    "평론가",     // 3레벨
    "분석가",     // 4레벨
    "영향력자",   // 5레벨
    "전문가",     // 6레벨
    "현자",       // 7레벨
    "대가",       // 8레벨
    "거장",       // 9레벨
    "인물의 신"   // 10레벨
  ];

  // 피보나치 수열 기반 레벨 요구 점수 계산
  function calculateLevelRequirement(level) {
    if (level <= 0) {
      return 0;
    }
    if (level === 1) {
      return 100;
    }
    if (level === 2) {
      return 200;
    }
    if (level === 3) {
      return 300;
    }
    if (level === 4) {
      return 500;
    }

    // 5레벨 이상은 피보나치로 계산
    let a = 300; // 3레벨 요구치
    let b = 500; // 4레벨 요구치
    let result = 0;

    for (let i = 5; i <= level; i++) {
      result = a + b;
      a = b;
      b = result;
    }

    return result;
  }

  // 사용자 레벨 및 진행도 계산
  function calculateUserLevel(prestige) {
    let level = 0;
    let accumulatedPoints = 0;
    let nextLevelRequirement = calculateLevelRequirement(level + 1);

    while (prestige >= accumulatedPoints + nextLevelRequirement) {
      accumulatedPoints += nextLevelRequirement;
      level++;
      nextLevelRequirement = calculateLevelRequirement(level + 1);
    }

    // 다음 레벨까지 남은 포인트
    const pointsForNextLevel = nextLevelRequirement;
    const currentLevelPoints = prestige - accumulatedPoints;
    const progressPercentage = Math.floor(
        (currentLevelPoints / pointsForNextLevel) * 100);

    return {
      level,
      currentLevelPoints,
      pointsForNextLevel,
      progressPercentage,
      totalPrestige: prestige,
      title: getLevelTitle(level)
    };
  }

  // 레벨에 따른 별명 반환
  function getLevelTitle(level) {
    if (level < 0) {
      return levelTitles[0];
    }
    if (level >= levelTitles.length) {
      return levelTitles[levelTitles.length - 1];
    }
    return levelTitles[level];
  }

  // 명성 레벨 표시 업데이트
  function updatePrestigeDisplay() {
    const prestigeElement = document.getElementById('userPrestige');
    if (!prestigeElement) {
      return;
    }

    const prestige = parseInt(
        prestigeElement.getAttribute('data-prestige') || '0');
    const levelInfo = calculateUserLevel(prestige);

    // 프로그레스 바 업데이트
    const progressBar = document.querySelector('.prestige-progress-bar');
    if (progressBar) {
      progressBar.style.width = `${levelInfo.progressPercentage}%`;
    }

    // 레벨 정보 업데이트
    const levelElement = document.getElementById('prestigeLevel');
    if (levelElement) {
      levelElement.textContent = levelInfo.level;
    }

    // 다음 레벨까지 필요한 포인트 업데이트
    const nextLevelPointsElement = document.getElementById(
        'pointsForNextLevel');
    if (nextLevelPointsElement) {
      nextLevelPointsElement.textContent = levelInfo.pointsForNextLevel
          - levelInfo.currentLevelPoints;
    }

    // 레벨 별명 업데이트
    const levelTitleElements = document.querySelectorAll('.level-title');
    if (levelTitleElements.length > 0) {
      levelTitleElements.forEach(element => {
        element.textContent = levelInfo.title;
      });
    }

    // 레벨에 따른 배지 스타일 업데이트
    const prestigeBadge = document.querySelector('.prestige-badge');
    if (prestigeBadge) {
      // 기존 레벨 클래스 제거
      for (let i = 0; i <= 10; i++) {
        prestigeBadge.classList.remove(`level-${i}`);
      }

      // 현재 레벨 클래스 추가
      prestigeBadge.classList.add(`level-${levelInfo.level}`);
    }

    // 레벨에 따른 배경색 업데이트
    const levelBadge = document.querySelector('.prestige-level-badge');
    if (levelBadge) {
      // 기존 레벨 클래스 제거
      for (let i = 0; i <= 10; i++) {
        levelBadge.classList.remove(`level-${i}`);
      }

      // 현재 레벨 클래스 추가
      levelBadge.classList.add(`level-${levelInfo.level}`);
    }

    // 프로그레스 바 섹션 업데이트
    const progressSection = document.querySelector(
        '.prestige-progress-section');
    if (progressSection) {
      // 기존 레벨 클래스 제거
      for (let i = 0; i <= 10; i++) {
        progressSection.classList.remove(`level-${i}`);
      }

      // 현재 레벨 클래스 추가
      progressSection.classList.add(`level-${levelInfo.level}`);
    }
  }

  // 모달 열기 함수
  function openModal(modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden'; // 배경 스크롤 방지

    // 모달 애니메이션을 위한 약간의 딜레이
    setTimeout(() => {
      const modalContent = modal.querySelector('.modal-content');
      if (modalContent) {
        modalContent.style.transform = 'scale(1) translateY(0)';
        modalContent.style.opacity = '1';
      }
    }, 50);
  }

  // 모달 닫기 함수
  function closeModal(modal) {
    const modalContent = modal.querySelector('.modal-content');
    if (modalContent) {
      modalContent.style.transform = 'scale(0.95) translateY(20px)';
      modalContent.style.opacity = '0';
    }

    // 애니메이션 후 모달 완전히 닫기
    setTimeout(() => {
      modal.classList.remove('active');
      document.body.style.overflow = ''; // 배경 스크롤 복원
    }, 200);
  }

  // 모든 모달 닫기
  function closeAllModals() {
    const modals = document.querySelectorAll('.modal-backdrop');
    modals.forEach(modal => closeModal(modal));
  }

  // 트리거 버튼에 이벤트 리스너 추가
  if (profileImageTrigger) {
    profileImageTrigger.addEventListener('click',
        () => openModal(profileImageModal));
  }

  if (editNicknameTrigger) {
    editNicknameTrigger.addEventListener('click',
        () => openModal(editNicknameModal));
  }

  if (withdrawTrigger) {
    withdrawTrigger.addEventListener('click', () => openModal(withdrawModal));
  }

  if (editBioTrigger) {
    editBioTrigger.addEventListener('click', () => openModal(editBioModal));
  }

  // 닫기 버튼에 이벤트 리스너 추가
  closeButtons.forEach(button => {
    button.addEventListener('click', () => {
      const modal = button.closest('.modal-backdrop');
      closeModal(modal);
    });
  });

  // 모달 외부 클릭 시 닫기
  document.querySelectorAll('.modal-backdrop').forEach(modal => {
    modal.addEventListener('click', (e) => {
      if (e.target === modal) {
        closeModal(modal);
      }
    });
  });

  // ESC 키로 모달 닫기
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      closeAllModals();
    }
  });

  // 프로필 카드와 통계 카드에 애니메이션 효과
  const infoCards = document.querySelectorAll('.info-card');

  infoCards.forEach((card, index) => {
    setTimeout(() => {
      card.classList.add('animate-fade-in');
    }, index * 100); // 각 카드에 시간차를 두고 표시
  });

  // 닉네임 유효성 검사
  const nicknameForm = document.querySelector(
      'form[action="/me/update-nickname"]');
  const nicknameInput = document.getElementById('nickname');

  if (nicknameForm && nicknameInput) {
    nicknameForm.addEventListener('submit', function (e) {
      const nickname = nicknameInput.value.trim();

      if (nickname.length < 2) {
        e.preventDefault();
        alert('닉네임은 최소 2자 이상이어야 합니다.');
        return false;
      }

      if (nickname.length > 20) {
        e.preventDefault();
        alert('닉네임은 최대 20자까지 가능합니다.');
        return false;
      }
    });
  }

  // 한줄 소개 유효성 검사 및 API 연동
  const bioForm = document.getElementById('updateBioForm');
  const bioInput = document.getElementById('bio');
  const bioCharCount = document.getElementById('bioCharCount');
  const bioText = document.getElementById('bioText');
  const bioEmptyText = document.querySelector('.bio-empty-text');

  if (bioForm && bioInput) {
    // 글자수 카운트 업데이트
    function updateCharCount() {
      const count = bioInput.value.length;
      bioCharCount.textContent = count;

      // 글자수 제한 (100자)
      if (count > 100) {
        bioCharCount.classList.add('text-red-500');
      } else {
        bioCharCount.classList.remove('text-red-500');
      }
    }

    // 초기 글자수 설정
    updateCharCount();

    // 입력할 때마다 글자수 업데이트
    bioInput.addEventListener('input', updateCharCount);

    // 폼 제출 처리
    bioForm.addEventListener('submit', function (e) {
      e.preventDefault();

      const bio = bioInput.value.trim();

      // 100자 초과 검사
      if (bio.length > 100) {
        alert('한줄 소개는 최대 100자까지 입력 가능합니다.');
        return;
      }

      // PATCH 요청 보내기
      fetch('/me/update-bio', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({bio: bio})
      })
      .then(response => {
        if (!response.ok) {
          throw new Error('서버 응답이 올바르지 않습니다.');
        }
        return response.json();
      })
      .then(data => {
        // 성공 처리
        // 모달 닫기
        closeModal(editBioModal);

        // UI 업데이트
        if (bioText) {
          bioText.textContent = bio;
        }

        // 빈 값인 경우 처리
        if (bio === '' && bioEmptyText) {
          bioEmptyText.style.display = 'block';
        } else if (bioEmptyText) {
          bioEmptyText.style.display = 'none';
        }

        // 성공 메시지 표시
        if (typeof ToastManager !== 'undefined') {
          ToastManager.success('한줄 소개가 업데이트되었습니다.');
        } else {
          alert('한줄 소개가 업데이트되었습니다.');
        }
      })
      .catch(error => {
        console.error('에러:', error);
        // 에러 메시지 표시
        if (typeof ToastManager !== 'undefined') {
          ToastManager.error('한줄 소개 업데이트에 실패했습니다.');
        } else {
          alert('한줄 소개 업데이트에 실패했습니다.');
        }
      });
    });
  }

  // 명성 레벨 표시 초기화
  updatePrestigeDisplay();

  // 레벨 정보 표시할 때 툴팁으로 상세 내용 표시
  const prestigeBadges = document.querySelectorAll(
      '.prestige-badge, .prestige-level-badge');
  prestigeBadges.forEach(badge => {
    badge.setAttribute('title', '레벨이 올라갈수록 다양한 혜택이 주어집니다!');
  });
});