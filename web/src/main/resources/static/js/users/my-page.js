document.addEventListener('DOMContentLoaded', function () {
  // 모달 요소들
  const profileImageModal = document.getElementById('profileImageModal');
  const editNicknameModal = document.getElementById('editNicknameModal');
  const withdrawModal = document.getElementById('withdrawModal');

  // 모달 트리거 버튼들
  const profileImageTrigger = document.getElementById('profileImageTrigger');
  const editNicknameTrigger = document.getElementById('editNicknameTrigger');
  const withdrawTrigger = document.getElementById('withdrawTrigger');

  // 모달 닫기 버튼들
  const closeButtons = document.querySelectorAll('.modal-close');

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
    profileImageTrigger.addEventListener('click', () => openModal(profileImageModal));
  }

  if (editNicknameTrigger) {
    editNicknameTrigger.addEventListener('click', () => openModal(editNicknameModal));
  }

  if (withdrawTrigger) {
    withdrawTrigger.addEventListener('click', () => openModal(withdrawModal));
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
  const nicknameForm = document.querySelector('form[action="/me/update-nickname"]');
  const nicknameInput = document.getElementById('nickname');

  if (nicknameForm && nicknameInput) {
    nicknameForm.addEventListener('submit', function(e) {
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
});