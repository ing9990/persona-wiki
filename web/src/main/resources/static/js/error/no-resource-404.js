/**
 * 차원의 경계 - 인터랙티브 404 페이지
 * 우주/몽환 테마로 구현된 예술적 404 페이지
 */

// 전역 변수
let canvas;
let ctx;
let stars = [];
let nebulae = [];
let mouseX = 0;
let mouseY = 0;
let isDragging = false;
let dragStartX = 0;
let dragStartY = 0;
let soundEnabled = false;
let particles = [];
let trailParticles = [];
let cursorSize = 20;
let lastTimeStamp = 0;
let frameCount = 0;

// DOM 요소 캐싱
const customCursor = document.getElementById('custom-cursor');
const cursorTrail = document.getElementById('cursor-trail');
const universeCanvas = document.getElementById('universe-canvas');
const particlesLayer = document.getElementById('particles-layer');
const messageArea = document.getElementById('message-area');
const homeButton = document.getElementById('home-button');
const warpButton = document.getElementById('warp-button');
const soundToggle = document.getElementById('sound-toggle');
const backgroundSound = document.getElementById('background-sound');
const clickSound = document.getElementById('click-sound');

// 초기화 함수
function init() {
  // 캔버스 설정
  canvas = universeCanvas;
  ctx = canvas.getContext('2d');
  resizeCanvas();

  // 이벤트 리스너 설정
  window.addEventListener('resize', resizeCanvas);
  document.addEventListener('mousemove', handleMouseMove);
  document.addEventListener('mousedown', handleMouseDown);
  document.addEventListener('mouseup', handleMouseUp);
  document.addEventListener('click', handleClick);

  // 버튼 이벤트 설정
  homeButton.addEventListener('click', goToHome);
  warpButton.addEventListener('click', performWarp);
  soundToggle.addEventListener('click', toggleSound);

  // 초기 별과 성운 생성
  createStars();
  createNebulae();

  // 애니메이션 시작
  requestAnimationFrame(animate);
}

// 캔버스 크기 조정
function resizeCanvas() {
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;
}

// 마우스 이동 처리
function handleMouseMove(e) {
  mouseX = e.clientX;
  mouseY = e.clientY;

  // 커스텀 커서 업데이트
  updateCustomCursor(e);

  // 드래그 중일 때 효과 적용
  if (isDragging) {
    const dragDistanceX = mouseX - dragStartX;
    const dragDistanceY = mouseY - dragStartY;

    // 별들에 드래그 효과 적용
    applyDragEffectToStars(dragDistanceX, dragDistanceY);

    // 메시지 영역 움직임
    messageArea.style.transform = `translate(calc(-50% + ${dragDistanceX
    * 0.02}px), calc(-50% + ${dragDistanceY * 0.02}px))`;
  }

  // 커서 주변 입자 생성
  createParticleNearCursor();
}

// 커스텀 커서 업데이트
function updateCustomCursor(e) {
  customCursor.style.left = `${e.clientX}px`;
  customCursor.style.top = `${e.clientY}px`;

  // 트레일 효과 생성
  createCursorTrail(e.clientX, e.clientY);
}

// 커서 트레일 생성
function createCursorTrail(x, y) {
  const trail = document.createElement('div');
  trail.className = 'cursor-trail';
  trail.style.left = `${x}px`;
  trail.style.top = `${y}px`;
  document.body.appendChild(trail);

  // 트레일 애니메이션
  gsap.to(trail, {
    opacity: 0.7,
    duration: 0.2,
    onComplete: () => {
      gsap.to(trail, {
        opacity: 0,
        duration: 0.8,
        onComplete: () => {
          trail.remove();
        }
      });
    }
  });

  // 트레일 움직임
  gsap.to(trail, {
    x: Math.random() * 40 - 20,
    y: Math.random() * 40 - 20,
    duration: 1
  });
}

// 마우스 다운 처리
function handleMouseDown(e) {
  isDragging = true;
  dragStartX = e.clientX;
  dragStartY = e.clientY;

  // 커서 크기 증가
  customCursor.style.width = '30px';
  customCursor.style.height = '30px';
  customCursor.style.backgroundColor = 'var(--accent)';

  // 클릭된 요소가 버튼이 아닌 경우에만 효과 적용
  if (!e.target.classList.contains('interactive-button')) {
    // 입자 폭발 효과
    createParticleExplosion(e.clientX, e.clientY);
  }
}

// 마우스 업 처리
function handleMouseUp() {
  isDragging = false;

  // 커서 크기 복원
  customCursor.style.width = '20px';
  customCursor.style.height = '20px';
  customCursor.style.backgroundColor = 'transparent';

  // 메시지 영역 위치 복원
  gsap.to(messageArea, {
    x: '-50%',
    y: '-50%',
    duration: 0.5,
    ease: 'elastic.out(1, 0.5)'
  });

  // 별들 위치 복원
  resetStarsPosition();
}

// 클릭 효과 생성
function handleClick(e) {
  // 소리 재생
  if (soundEnabled) {
    playClickSound();
  }

  // 클릭 파동 효과
  createRippleEffect(e.clientX, e.clientY);

  // 랜덤 도형 생성 (버튼 클릭이 아닌 경우에만)
  if (!e.target.classList.contains('interactive-button')) {
    createRandomShape(e.clientX, e.clientY);
  }
}

// 클릭 파동 효과
function createRippleEffect(x, y) {
  const ripple = document.createElement('div');
  ripple.className = 'click-effect';
  ripple.style.left = `${x}px`;
  ripple.style.top = `${y}px`;
  document.body.appendChild(ripple);

  // 애니메이션 완료 후 제거
  setTimeout(() => {
    ripple.remove();
  }, 1500);
}

// 랜덤 도형 생성
function createRandomShape(x, y) {
  const shape = document.createElement('div');
  shape.className = 'generated-shape';

  // 랜덤 도형 결정
  const shapeType = Math.floor(Math.random() * 4);
  const size = Math.random() * 80 + 40;
  const hue = Math.floor(Math.random() * 60) + 240; // 파란색~보라색 계열

  shape.style.position = 'absolute';
  shape.style.left = `${x - size / 2}px`;
  shape.style.top = `${y - size / 2}px`;
  shape.style.width = `${size}px`;
  shape.style.height = `${size}px`;

  // 도형 스타일
  if (shapeType === 0) {
    // 원
    shape.style.borderRadius = '50%';
    shape.style.background = `hsla(${hue}, 80%, 60%, 0.6)`;
  } else if (shapeType === 1) {
    // 사각형
    shape.style.background = `hsla(${hue}, 80%, 60%, 0.6)`;
    shape.style.transform = `rotate(${Math.random() * 45}deg)`;
  } else if (shapeType === 2) {
    // 별
    shape.style.background = 'transparent';
    shape.style.boxShadow = `0 0 20px hsla(${hue}, 80%, 60%, 0.8)`;
    shape.innerHTML = createStarSVG(size, hue);
  } else {
    // 삼각형
    shape.style.background = 'transparent';
    shape.style.borderLeft = `${size / 2}px solid transparent`;
    shape.style.borderRight = `${size / 2}px solid transparent`;
    shape.style.borderBottom = `${size}px solid hsla(${hue}, 80%, 60%, 0.6)`;
    shape.style.width = '0';
    shape.style.height = '0';
  }

  document.body.appendChild(shape);

  // 애니메이션 및 제거
  gsap.to(shape, {
    x: Math.random() * 200 - 100,
    y: Math.random() * 200 - 100,
    opacity: 0,
    duration: 3 + Math.random() * 2,
    ease: 'power1.out',
    onComplete: () => {
      shape.remove();
    }
  });
}

// SVG 별 생성
function createStarSVG(size, hue) {
  const points = [];
  const outerRadius = size / 2;
  const innerRadius = outerRadius / 2;
  const numPoints = 5;

  for (let i = 0; i < numPoints * 2; i++) {
    const radius = i % 2 === 0 ? outerRadius : innerRadius;
    const angle = (Math.PI / numPoints) * i;
    const x = outerRadius + radius * Math.sin(angle);
    const y = outerRadius - radius * Math.cos(angle);
    points.push(`${x},${y}`);
  }

  return `<svg width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
    <polygon points="${points.join(' ')}" fill="hsla(${hue}, 80%, 60%, 0.6)" />
  </svg>`;
}

// 커서 주변 입자 생성
function createParticleNearCursor() {
  if (Math.random() > 0.7) {
    const offsetX = (Math.random() - 0.5) * 40;
    const offsetY = (Math.random() - 0.5) * 40;
    const size = Math.random() * 4 + 1;
    const opacity = Math.random() * 0.5 + 0.3;
    const lifetime = Math.random() * 2000 + 1000;

    const particle = document.createElement('div');
    particle.className = 'particle';
    particle.style.width = `${size}px`;
    particle.style.height = `${size}px`;
    particle.style.left = `${mouseX + offsetX}px`;
    particle.style.top = `${mouseY + offsetY}px`;
    particle.style.opacity = opacity;

    particlesLayer.appendChild(particle);

    // 입자 움직임 애니메이션
    gsap.to(particle, {
      x: offsetX * 3,
      y: offsetY * 3,
      opacity: 0,
      duration: lifetime / 1000,
      ease: 'power1.out',
      onComplete: () => {
        particle.remove();
      }
    });
  }
}

// 입자 폭발 효과
function createParticleExplosion(x, y) {
  const particleCount = 30;

  for (let i = 0; i < particleCount; i++) {
    const size = Math.random() * 6 + 2;
    const angle = Math.random() * Math.PI * 2;
    const speed = Math.random() * 5 + 3;
    const lifetime = Math.random() * 1000 + 500;

    const particle = document.createElement('div');
    particle.className = 'particle';
    particle.style.width = `${size}px`;
    particle.style.height = `${size}px`;
    particle.style.left = `${x}px`;
    particle.style.top = `${y}px`;

    particlesLayer.appendChild(particle);

    // 폭발 애니메이션
    gsap.to(particle, {
      x: Math.cos(angle) * speed * 20,
      y: Math.sin(angle) * speed * 20,
      opacity: 0,
      duration: lifetime / 1000,
      ease: 'power2.out',
      onComplete: () => {
        particle.remove();
      }
    });
  }
}

// 별 생성
function createStars() {
  stars = [];
  const starCount = Math.min(window.innerWidth, window.innerHeight) / 3;

  for (let i = 0; i < starCount; i++) {
    stars.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      size: Math.random() * 2 + 0.5,
      brightness: Math.random(),
      originalX: 0,
      originalY: 0,
      speed: Math.random() * 0.05 + 0.02
    });

    // 원래 위치 저장
    stars[i].originalX = stars[i].x;
    stars[i].originalY = stars[i].y;
  }
}

// 성운 생성
function createNebulae() {
  nebulae = [];
  const nebulaCount = 3;

  for (let i = 0; i < nebulaCount; i++) {
    nebulae.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      radius: Math.random() * 200 + 100,
      hue: Math.random() * 60 + 240, // 파란색~보라색 계열
      alpha: Math.random() * 0.1 + 0.05
    });
  }
}

// 별들에 드래그 효과 적용
function applyDragEffectToStars(dragX, dragY) {
  for (let i = 0; i < stars.length; i++) {
    const distance = Math.sqrt(
        Math.pow((stars[i].originalX - mouseX), 2) +
        Math.pow((stars[i].originalY - mouseY), 2)
    );
    const maxDistance = 300;

    if (distance < maxDistance) {
      const force = (maxDistance - distance) / maxDistance;
      stars[i].x = stars[i].originalX + (dragX * force * 0.05);
      stars[i].y = stars[i].originalY + (dragY * force * 0.05);
    }
  }
}

// 별 위치 리셋
function resetStarsPosition() {
  for (let i = 0; i < stars.length; i++) {
    gsap.to(stars[i], {
      x: stars[i].originalX,
      y: stars[i].originalY,
      duration: 1,
      ease: 'elastic.out(1, 0.3)'
    });
  }
}

// 별 그리기
function drawStars() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  // 배경 그라데이션
  const gradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height);
  gradient.addColorStop(0, 'rgba(5, 0, 20, 1)');
  gradient.addColorStop(1, 'rgba(25, 0, 50, 1)');
  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  // 성운 그리기
  drawNebulae();

  // 별 그리기
  for (let i = 0; i < stars.length; i++) {
    const star = stars[i];

    // 시간에 따라 별 깜박임 효과
    const flickerAmount = Math.sin(Date.now() * star.speed) * 0.2 + 0.8;
    const alpha = star.brightness * flickerAmount;

    // 커서와의 거리에 따른 별 크기 변화
    const dx = mouseX - star.x;
    const dy = mouseY - star.y;
    const distance = Math.sqrt(dx * dx + dy * dy);
    const maxDistance = 150;
    let sizeMultiplier = 1;

    if (distance < maxDistance) {
      sizeMultiplier = 1 + ((maxDistance - distance) / maxDistance) * 1.5;
    }

    // 별 그리기
    ctx.beginPath();
    ctx.arc(star.x, star.y, star.size * sizeMultiplier, 0, Math.PI * 2);
    ctx.fillStyle = `rgba(255, 255, 255, ${alpha})`;
    ctx.fill();

    // 별 주변 빛 효과
    if (star.size > 1) {
      ctx.beginPath();
      ctx.arc(star.x, star.y, star.size * sizeMultiplier * 2, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(255, 255, 255, ${alpha * 0.3})`;
      ctx.fill();
    }
  }
}

// 성운 그리기
function drawNebulae() {
  for (let i = 0; i < nebulae.length; i++) {
    const nebula = nebulae[i];
    const gradient = ctx.createRadialGradient(
        nebula.x, nebula.y, 0,
        nebula.x, nebula.y, nebula.radius
    );

    // 성운의 색상 그라데이션
    gradient.addColorStop(0, `hsla(${nebula.hue}, 100%, 70%, ${nebula.alpha})`);
    gradient.addColorStop(0.4,
        `hsla(${nebula.hue + 20}, 100%, 60%, ${nebula.alpha * 0.6})`);
    gradient.addColorStop(1, 'rgba(0, 0, 0, 0)');

    ctx.fillStyle = gradient;
    ctx.beginPath();
    ctx.arc(nebula.x, nebula.y, nebula.radius, 0, Math.PI * 2);
    ctx.fill();

    // 시간에 따른 성운 움직임
    nebula.x += Math.sin(Date.now() * 0.0001) * 0.2;
    nebula.y += Math.cos(Date.now() * 0.0001) * 0.2;
  }
}

// 애니메이션 함수
function animate(timestamp) {
  // 프레임 속도 제한
  if (timestamp - lastTimeStamp >= 16) { // 약 60fps
    lastTimeStamp = timestamp;
    frameCount++;

    // 별과 성운 그리기
    drawStars();

    // 10초마다 성운 위치 업데이트
    if (frameCount % 600 === 0) {
      for (let i = 0; i < nebulae.length; i++) {
        gsap.to(nebulae[i], {
          x: Math.random() * canvas.width,
          y: Math.random() * canvas.height,
          duration: 10,
          ease: 'power1.inOut'
        });
      }
    }
  }

  requestAnimationFrame(animate);
}

// 홈으로 돌아가기
function goToHome() {
  window.location.href = '/';
}

// 랜덤 워프 효과
function performWarp() {
  // 화면 번쩍임 효과
  const flash = document.createElement('div');
  flash.style.position = 'fixed';
  flash.style.top = '0';
  flash.style.left = '0';
  flash.style.width = '100vw';
  flash.style.height = '100vh';
  flash.style.backgroundColor = 'white';
  flash.style.opacity = '0';
  flash.style.zIndex = '9999';
  flash.style.pointerEvents = 'none';
  document.body.appendChild(flash);

  // 플래시 애니메이션
  gsap.to(flash, {
    opacity: 0.8,
    duration: 0.2,
    onComplete: () => {
      // 별 재배치
      for (let i = 0; i < stars.length; i++) {
        stars[i].x = Math.random() * canvas.width;
        stars[i].y = Math.random() * canvas.height;
        stars[i].originalX = stars[i].x;
        stars[i].originalY = stars[i].y;
      }

      // 성운 재배치
      for (let i = 0; i < nebulae.length; i++) {
        nebulae[i].x = Math.random() * canvas.width;
        nebulae[i].y = Math.random() * canvas.height;
        nebulae[i].hue = Math.random() * 60 + 240;
      }

      // 메시지 변경
      const messages = [
        "404 — 이 공간은 현실에 존재하지 않지만, 당신의 상상 속에 존재할 수 있어요.",
        "404 — 길을 잃었지만, 우주를 떠돌며 새로운 걸 발견할 수 있어요.",
        "404 — 무한한 우주 속, 존재하지 않는 공간의 아름다움을 느껴보세요.",
        "404 — 당신은 지금 차원의 틈새를 여행하고 있습니다.",
        "404 — 잃어버린 공간 속에서 새로운 가능성을 찾아보세요."
      ];

      const subMessages = [
        "클릭해보세요. 이 세계는 반응합니다.",
        "이 페이지는 의미를 전달하지 않지만, 의미 없이 머물 수 있는 공간입니다.",
        "이곳에서 잠시 명상하듯 머물러 보세요.",
        "빛과 색의 흐름 속에서 잠시 여행해보세요.",
        "시간은 흐르지만, 이 공간에서는 멈춘 것처럼 느껴집니다."
      ];

      const randomIndex1 = Math.floor(Math.random() * messages.length);
      const randomIndex2 = Math.floor(Math.random() * subMessages.length);

      document.querySelector(
          '.main-message').textContent = messages[randomIndex1];
      document.querySelector(
          '.sub-message').textContent = subMessages[randomIndex2];

      // 플래시 페이드 아웃
      gsap.to(flash, {
        opacity: 0,
        duration: 0.5,
        delay: 0.2,
        onComplete: () => {
          flash.remove();
        }
      });
    }
  });

  // 사운드 재생
  if (soundEnabled) {
    const warpSound = new Audio('/audio/warp-sound.mp3');
    warpSound.volume = 0.5;
    warpSound.play();
  }
}

// 소리 토글
function toggleSound() {
  soundEnabled = !soundEnabled;

  // 버튼 텍스트 변경
  soundToggle.textContent = soundEnabled ? '소리 끄기' : '소리 켜기';

  // 배경 음악 제어
  if (soundEnabled) {
    backgroundSound.volume = 0.3;
    backgroundSound.play();
  } else {
    backgroundSound.pause();
  }
}

// 클릭 소리 재생
function playClickSound() {
  clickSound.currentTime = 0;
  clickSound.volume = 0.2;
  clickSound.play();
}

// 페이지 로드 시 초기화
window.addEventListener('load', init);