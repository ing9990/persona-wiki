/**
 * 인물 프로필 이미지 및 Bio 모달 관련 스크립트
 */
document.addEventListener('DOMContentLoaded', function() {
  // 이미지 모달 관련 요소들
  const profileImage = document.getElementById('profile-image-toggle');
  const bioToggle = document.getElementById('bio-toggle');
  const bioModal = document.getElementById('bio-modal');

  // 바이오 모달 기능은 기존 코드로 유지
  if (bioToggle && bioModal) {
    const bioModalClose = document.getElementById('bio-modal-close');
    const bioModalBackdrop = document.getElementById('bio-modal-backdrop');

    // 모달 열기
    bioToggle.addEventListener('click', function() {
      bioModal.classList.remove('hidden');
      setTimeout(() => {
        bioModal.classList.add('show');
      }, 10);
      document.body.style.overflow = 'hidden'; // 스크롤 방지
    });

    // 모달 닫기 함수
    const closeBioModal = function() {
      bioModal.classList.remove('show');
      setTimeout(() => {
        bioModal.classList.add('hidden');
        document.body.style.overflow = ''; // 스크롤 복원
      }, 300);
    };

    // 닫기 버튼 이벤트
    if (bioModalClose) {
      bioModalClose.addEventListener('click', closeBioModal);
    }

    // 배경 클릭 시 모달 닫기
    if (bioModalBackdrop) {
      bioModalBackdrop.addEventListener('click', closeBioModal);
    }

    // ESC 키 누를 때 모달 닫기
    document.addEventListener('keydown', function(e) {
      if (e.key === 'Escape' && !bioModal.classList.contains('hidden')) {
        closeBioModal();
      }
    });
  }

  // 이미지 모달 생성 및 초기화
  createImageModal();

  // 프로필 이미지 클릭 이벤트
  if (profileImage) {
    profileImage.addEventListener('click', function() {
      openImageModal(this.src, this.alt);
    });
  }

  // 헤더 이미지 패럴랙스 효과
  const profileHeader = document.querySelector('.profile-header');
  const headerImage = document.querySelector('.profile-header-image');

  if (profileHeader && headerImage) {
    window.addEventListener('scroll', function() {
      // 이미지 패럴랙스 효과
      const scrollPosition = window.scrollY;
      if (scrollPosition < 300) { // 스크롤이 너무 내려가면 효과 중단
        headerImage.style.transform = `translateY(${scrollPosition * 0.2}px) scale(${1 + scrollPosition * 0.0005})`;
      }
    });
  }
});

/**
 * 이미지 모달 요소 생성
 */
function createImageModal() {
  // 이미 모달이 있으면 생성하지 않음
  if (document.getElementById('image-modal')) {
    return;
  }

  // 모달 요소 생성
  const modal = document.createElement('div');
  modal.id = 'image-modal';
  modal.innerHTML = `
    <span id="image-modal-close">&times;</span>
    <img class="modal-content" id="modal-img">
    <div id="modal-caption" class="modal-caption"></div>
  `;

  // 모달 닫기 기능 추가
  document.body.appendChild(modal);

  const closeBtn = document.getElementById('image-modal-close');
  closeBtn.addEventListener('click', closeImageModal);

  // 배경 클릭 시 모달 닫기
  modal.addEventListener('click', function(e) {
    if (e.target === modal) {
      closeImageModal();
    }
  });

  // ESC 키 누를 때 모달 닫기
  document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape' && modal.classList.contains('show')) {
      closeImageModal();
    }
  });

  // 터치 스와이프로 모달 닫기 (모바일 최적화)
  let touchStartX = 0;
  let touchEndX = 0;

  modal.addEventListener('touchstart', function(e) {
    touchStartX = e.changedTouches[0].screenX;
  }, false);

  modal.addEventListener('touchend', function(e) {
    touchEndX = e.changedTouches[0].screenX;
    handleSwipe();
  }, false);

  function handleSwipe() {
    if (touchEndX - touchStartX > 100) { // 오른쪽 스와이프
      closeImageModal();
    } else if (touchStartX - touchEndX > 100) { // 왼쪽 스와이프
      closeImageModal();
    }
  }
}

/**
 * 이미지 모달 열기
 * @param {string} imgSrc - 이미지 소스 URL
 * @param {string} imgAlt - 이미지 대체 텍스트
 */
function openImageModal(imgSrc, imgAlt) {
  const modal = document.getElementById('image-modal');
  const modalImg = document.getElementById('modal-img');
  const captionText = document.getElementById('modal-caption');

  if (!modal || !modalImg) return;

  // 고화질 이미지 로드 (가능한 경우)
  // 원본 이미지 URL에서 고화질 버전이 있다면 사용
  const imgSrcOriginal = imgSrc.replace(/\/thumbnail\//, '/original/');

  // 이미지 로딩 애니메이션 표시
  modal.classList.remove('hidden');
  modal.classList.add('show');
  modalImg.style.opacity = '0.3';
  modalImg.src = imgSrcOriginal || imgSrc;

  // 이미지가 로드된 후 표시
  modalImg.onload = function() {
    modalImg.style.opacity = '1';
    modalImg.style.transition = 'opacity 0.3s ease';
  };

  captionText.innerText = imgAlt || '인물 이미지';
  document.body.style.overflow = 'hidden'; // 스크롤 방지
}

/**
 * 이미지 모달 닫기
 */
function closeImageModal() {
  const modal = document.getElementById('image-modal');
  if (!modal) return;

  modal.classList.remove('show');
  setTimeout(() => {
    modal.classList.add('hidden');
    document.body.style.overflow = ''; // 스크롤 복원
  }, 300);
}