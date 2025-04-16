// 애니메이션 관련 유틸리티 함수들
document.addEventListener('DOMContentLoaded', function () {
  // SVG 아이콘 플레이스홀더를 body에 추가
  createSVGPlaceholders();
});

// SVG 아이콘 플레이스홀더를 생성하는 함수
function createSVGPlaceholders() {
  const placeholderDiv = document.createElement('div');
  placeholderDiv.className = 'placeholder-svg';
  placeholderDiv.innerHTML = `
    <!-- 애니메이션용 SVG 생성 -->
    <svg id="light-rays" xmlns="http://www.w3.org/2000/svg" width="0" height="0">
      <defs>
        <radialGradient id="ray-gradient" cx="50%" cy="50%" r="50%" fx="50%" fy="50%">
          <stop offset="0%" stop-color="gold" stop-opacity="0.8"/>
          <stop offset="70%" stop-color="gold" stop-opacity="0"/>
        </radialGradient>
      </defs>
      <circle cx="50%" cy="50%" r="50%" fill="url(#ray-gradient)">
        <animate attributeName="r" values="0;100" dur="1.5s" repeatCount="1"/>
        <animate attributeName="opacity" values="0;0.8;0" dur="1.5s" repeatCount="1"/>
      </circle>
    </svg>
    
    <svg id="angel-wings" xmlns="http://www.w3.org/2000/svg" width="0" height="0">
      <path d="M50,50 Q60,20 80,10 T100,30 T90,50 T100,70 T80,90 T50,50 Q40,20 20,10 T0,30 T10,50 T0,70 T20,90 T50,50" fill="rgba(255, 215, 0, 0.7)" stroke="gold" stroke-width="2"/>
    </svg>
    
    <svg id="flames" xmlns="http://www.w3.org/2000/svg" width="0" height="0">
      <defs>
        <radialGradient id="flame-gradient" cx="50%" cy="50%" r="50%" fx="50%" fy="50%">
          <stop offset="0%" stop-color="#ff3d00" stop-opacity="0.9"/>
          <stop offset="70%" stop-color="#ff3d00" stop-opacity="0"/>
        </radialGradient>
      </defs>
      <path d="M50,0 Q60,40 75,25 Q85,45 60,80 Q75,70 85,80 Q75,100 50,85 Q25,100 15,80 Q25,70 40,80 Q15,45 25,25 Q40,40 50,0" fill="url(#flame-gradient)"/>
    </svg>
    
    <svg id="prison-bars" xmlns="http://www.w3.org/2000/svg" width="0" height="0">
      <pattern id="bar-pattern" width="20" height="100" patternUnits="userSpaceOnUse">
        <rect x="8" y="0" width="4" height="100" fill="#333"/>
      </pattern>
      <rect width="100%" height="100%" fill="url(#bar-pattern)"/>
    </svg>
  `;

  document.body.appendChild(placeholderDiv);
}

// 3D Flip 효과 적용 함수
function applyFlipEffect(element) {
  if (element) {
    element.classList.add('flip');

    // 애니메이션 끝난 후 클래스 제거
    setTimeout(() => {
      element.classList.remove('flip');
    }, 500); // 0.5초 후 제거
  }
}

// 풀스크린 애니메이션 생성 함수
function createFullscreenAnimation(sentiment) {
  // 이미 존재하는 풀스크린 애니메이션 제거
  removeFullscreenAnimation();

  // 새로운 애니메이션 컨테이너 생성
  const animationContainer = document.createElement('div');
  animationContainer.id = 'fullscreen-animation';
  animationContainer.className = 'fullscreen-animation';

  // 투표 타입에 따른 애니메이션 설정
  if (sentiment === 'POSITIVE') {
    animationContainer.classList.add('worship-animation');
    animationContainer.innerHTML = '<div class="wings"></div>';
  } else if (sentiment === 'NEUTRAL') {
    animationContainer.classList.add('neutral-animation');
    animationContainer.innerHTML = `
      <div class="scale">
        <div class="scale-center"></div>
      </div>
    `;
  } else if (sentiment === 'NEGATIVE') {
    animationContainer.classList.add('punish-animation');
    animationContainer.innerHTML = `
      <div class="flames"></div>
      <div class="bars"></div>
    `;
  }

  // 애니메이션 컨테이너를 body에 추가
  document.body.appendChild(animationContainer);

  // 애니메이션 타이머 설정 (애니메이션 후 제거)
  setTimeout(removeFullscreenAnimation, 1500); // 1.5초 후 제거
}

// 풀스크린 애니메이션 제거 함수
function removeFullscreenAnimation() {
  const existingAnimation = document.getElementById('fullscreen-animation');
  if (existingAnimation) {
    existingAnimation.remove();
  }
}

// 사용자 투표를 강조하는 함수
function highlightUserVote(sentiment) {
  // 기존 결과 컨테이너에서 해당 감정 표시 강조
  const sentimentBadges = document.querySelectorAll('.vote-sentiment-badge');
  sentimentBadges.forEach(badge => {
    badge.classList.remove('user-vote-highlight');

    // 사용자가 선택한 감정 타입에 강조 추가
    if ((sentiment === 'POSITIVE' && badge.classList.contains(
            'sentiment-positive')) ||
        (sentiment === 'NEUTRAL' && badge.classList.contains(
            'sentiment-neutral')) ||
        (sentiment === 'NEGATIVE' && badge.classList.contains(
            'sentiment-negative'))) {
      badge.classList.add('user-vote-highlight');
    }
  });
}

// 외부에 노출할 함수들
window.voteAnimations = {
  flipButton: applyFlipEffect,
  showFullscreenAnimation: createFullscreenAnimation,
  highlightUserVote: highlightUserVote
};