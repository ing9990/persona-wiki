document.addEventListener('DOMContentLoaded', () => {
  console.log('DOM이 로드되었습니다'); // 디버깅 로그 추가

  // 테마 관련 초기화
  initTheme();

  // 404 애니메이션 초기화
  initAnimation();

  // 검색 기능 초기화
  initSearch();

  // 팝콘 파티클 효과 초기화
  initConfetti();

  // 추천 인물 버튼에 호버 효과 추가
  initRecommendButtons();

  // 디버깅 정보
  console.log('모든 초기화가 완료되었습니다');
  console.log('검색 버튼 요소:', document.getElementById('search-btn'));
  console.log('검색 패널 요소:', document.getElementById('search-panel'));
});

/**
 * 추천 인물 버튼에 호버 효과 추가
 */
function initRecommendButtons() {
  // 모든 추천 버튼 요소
  const recommendButtons = document.querySelectorAll('.max-w-3xl a');

  recommendButtons.forEach(button => {
    // 각 버튼에 마우스 진입 시 효과
    button.addEventListener('mouseenter', () => {
      gsap.to(button, {
        y: -5,
        boxShadow: '0 8px 15px rgba(0, 0, 0, 0.1)',
        duration: 0.3,
        ease: 'power2.out'
      });
    });

    // 마우스 떠날 때 원래대로
    button.addEventListener('mouseleave', () => {
      gsap.to(button, {
        y: 0,
        boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
        duration: 0.3,
        ease: 'power2.out'
      });
    });
  });
}

/**
 * 다크/라이트 모드 테마 초기화 및 토글 기능
 */
function initTheme() {
  const themeToggleBtn = document.getElementById('theme-toggle');
  const moonIcon = document.getElementById('moon-icon');
  const sunIcon = document.getElementById('sun-icon');
  const body = document.body;

  // 저장된 테마 또는 시스템 환경 설정에 따라 초기 테마 설정
  const savedTheme = localStorage.getItem('theme');
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

  if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
    body.setAttribute('data-theme', 'dark');
    moonIcon.classList.add('hidden');
    sunIcon.classList.remove('hidden');
  }

  // 테마 토글 클릭 이벤트
  themeToggleBtn.addEventListener('click', () => {
    if (body.getAttribute('data-theme') === 'dark') {
      body.setAttribute('data-theme', 'light');
      moonIcon.classList.remove('hidden');
      sunIcon.classList.add('hidden');
      localStorage.setItem('theme', 'light');
    } else {
      body.setAttribute('data-theme', 'dark');
      moonIcon.classList.add('hidden');
      sunIcon.classList.remove('hidden');
      localStorage.setItem('theme', 'dark');
    }

    // 애니메이션 색상 업데이트
    updateAnimationColors();
  });
}

/**
 * SVG 애니메이션 초기화
 */
function initAnimation() {
  const canvas = document.getElementById('animation-canvas');

  // SVG 네임스페이스
  const svgNS = "http://www.w3.org/2000/svg";

  // SVG 요소 생성
  const svg = document.createElementNS(svgNS, "svg");
  svg.setAttribute("viewBox", "0 0 800 450");
  svg.setAttribute("width", "100%");
  svg.setAttribute("height", "100%");
  svg.setAttribute("id", "error-animation");
  canvas.appendChild(svg);

  // 그라디언트 정의
  const defs = document.createElementNS(svgNS, "defs");
  defs.innerHTML = `
    <linearGradient id="grid-gradient" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="rgba(79, 70, 229, 0.1)" class="grid-start-color" />
      <stop offset="100%" stop-color="rgba(79, 70, 229, 0.05)" class="grid-end-color" />
    </linearGradient>
    <linearGradient id="path-gradient" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" stop-color="#4f46e5" class="path-start-color" />
      <stop offset="100%" stop-color="#8b5cf6" class="path-end-color" />
    </linearGradient>
    <filter id="glow">
      <feGaussianBlur stdDeviation="2" result="blur" />
      <feComposite in="SourceGraphic" in2="blur" operator="over" />
    </filter>
  `;
  svg.appendChild(defs);

  // 그리드 배경 만들기
  createGrid(svg, 16, 9, 50);

  // 404 텍스트 추가
  const text404 = document.createElementNS(svgNS, "text");
  text404.setAttribute("x", "400");
  text404.setAttribute("y", "200");
  text404.setAttribute("text-anchor", "middle");
  text404.setAttribute("dominant-baseline", "middle");
  text404.setAttribute("font-size", "180");
  text404.setAttribute("font-weight", "bold");
  text404.setAttribute("fill-opacity", "0.08");
  text404.setAttribute("class", "text-404");
  text404.textContent = "404";
  svg.appendChild(text404);

  // 미로 경로 생성
  createMazePath(svg);

  // 캐릭터 추가
  createCharacter(svg);

  // 별 추가
  createStars(svg, 20);

  // 애니메이션 시작
  startAnimation();

  // 초기 테마에 맞게 색상 설정
  updateAnimationColors();
}

/**
 * 배경에 그리드 패턴 생성
 */
function createGrid(svg, columns, rows, size) {
  const svgNS = "http://www.w3.org/2000/svg";
  const gridGroup = document.createElementNS(svgNS, "g");
  gridGroup.setAttribute("class", "grid-pattern");

  // 수평 라인
  for (let i = 0; i <= rows; i++) {
    const y = i * size;
    const line = document.createElementNS(svgNS, "line");
    line.setAttribute("x1", "0");
    line.setAttribute("y1", y);
    line.setAttribute("x2", columns * size);
    line.setAttribute("y2", y);
    line.setAttribute("stroke", "currentColor");
    line.setAttribute("stroke-width", "0.5");
    line.setAttribute("stroke-opacity", "0.1");
    gridGroup.appendChild(line);
  }

  // 수직 라인
  for (let i = 0; i <= columns; i++) {
    const x = i * size;
    const line = document.createElementNS(svgNS, "line");
    line.setAttribute("x1", x);
    line.setAttribute("y1", "0");
    line.setAttribute("x2", x);
    line.setAttribute("y2", rows * size);
    line.setAttribute("stroke", "currentColor");
    line.setAttribute("stroke-width", "0.5");
    line.setAttribute("stroke-opacity", "0.1");
    gridGroup.appendChild(line);
  }

  svg.appendChild(gridGroup);
}

/**
 * 미로 경로 생성
 */
function createMazePath(svg) {
  const svgNS = "http://www.w3.org/2000/svg";

  // 복잡한 곡선 경로
  const path = document.createElementNS(svgNS, "path");
  path.setAttribute("d",
      "M100,350 C150,300 200,250 250,300 S350,350 400,250 S500,150 600,200 S700,300 700,200");
  path.setAttribute("fill", "none");
  path.setAttribute("stroke", "url(#path-gradient)");
  path.setAttribute("stroke-width", "4");
  path.setAttribute("stroke-linecap", "round");
  path.setAttribute("stroke-dasharray", "1500");
  path.setAttribute("stroke-dashoffset", "1500");
  path.setAttribute("filter", "url(#glow)");
  path.setAttribute("class", "maze-path");
  path.setAttribute("id", "maze-path");
  svg.appendChild(path);
}

/**
 * 움직이는 캐릭터 생성
 */
function createCharacter(svg) {
  const svgNS = "http://www.w3.org/2000/svg";

  // 캐릭터 그룹
  const characterGroup = document.createElementNS(svgNS, "g");
  characterGroup.setAttribute("id", "character");
  characterGroup.setAttribute("transform", "translate(100, 350)");

  // 캐릭터 몸통 (원)
  const body = document.createElementNS(svgNS, "circle");
  body.setAttribute("cx", "0");
  body.setAttribute("cy", "0");
  body.setAttribute("r", "15");
  body.setAttribute("fill", "#4f46e5");
  body.setAttribute("class", "character-body");
  characterGroup.appendChild(body);

  // 캐릭터 얼굴
  const face = document.createElementNS(svgNS, "g");
  face.setAttribute("class", "character-face");

  // 왼쪽 눈
  const leftEye = document.createElementNS(svgNS, "circle");
  leftEye.setAttribute("cx", "-5");
  leftEye.setAttribute("cy", "-4");
  leftEye.setAttribute("r", "2.5");
  leftEye.setAttribute("fill", "white");
  face.appendChild(leftEye);

  // 오른쪽 눈
  const rightEye = document.createElementNS(svgNS, "circle");
  rightEye.setAttribute("cx", "5");
  rightEye.setAttribute("cy", "-4");
  rightEye.setAttribute("r", "2.5");
  rightEye.setAttribute("fill", "white");
  face.appendChild(rightEye);

  // 웃는 입
  const mouth = document.createElementNS(svgNS, "path");
  mouth.setAttribute("d", "M-6,5 Q0,12 6,5");
  mouth.setAttribute("fill", "none");
  mouth.setAttribute("stroke", "white");
  mouth.setAttribute("stroke-width", "2");
  mouth.setAttribute("stroke-linecap", "round");
  face.appendChild(mouth);

  characterGroup.appendChild(face);

  // 동글동글한 빛
  const glow = document.createElementNS(svgNS, "circle");
  glow.setAttribute("cx", "0");
  glow.setAttribute("cy", "0");
  glow.setAttribute("r", "20");
  glow.setAttribute("fill", "rgba(79, 70, 229, 0.3)");
  glow.setAttribute("filter", "url(#glow)");
  glow.setAttribute("class", "character-glow");

  // 글로우를 캐릭터 밑에 배치
  characterGroup.insertBefore(glow, body);

  svg.appendChild(characterGroup);
}

/**
 * 깜빡이는 별 추가
 */
function createStars(svg, count) {
  const svgNS = "http://www.w3.org/2000/svg";
  const starGroup = document.createElementNS(svgNS, "g");
  starGroup.setAttribute("class", "stars");

  for (let i = 0; i < count; i++) {
    const x = Math.random() * 800;
    const y = Math.random() * 450;
    const size = Math.random() * 3 + 1;

    const star = document.createElementNS(svgNS, "circle");
    star.setAttribute("cx", x);
    star.setAttribute("cy", y);
    star.setAttribute("r", size);
    star.setAttribute("fill", "#4f46e5");
    star.setAttribute("class", "star");
    star.setAttribute("fill-opacity", Math.random() * 0.5 + 0.2);

    // 깜빡임 애니메이션을 위한 속성
    star.dataset.twinkleSpeed = Math.random() * 3 + 1;
    star.dataset.twinkleDelay = Math.random() * 5;

    starGroup.appendChild(star);
  }

  svg.appendChild(starGroup);
}

/**
 * 애니메이션 시작
 */
function startAnimation() {
  // 경로 애니메이션
  gsap.to('.maze-path', {
    strokeDashoffset: 0,
    duration: 4,
    ease: "power1.inOut"
  });

  // 404 텍스트 애니메이션
  gsap.from('.text-404', {
    opacity: 0,
    y: 30,
    duration: 1.5,
    delay: 0.5
  });

  // 캐릭터 애니메이션
  const path = document.getElementById('maze-path');
  const character = document.getElementById('character');

  gsap.to(character, {
    duration: 6,
    ease: "power1.inOut",
    motionPath: {
      path: path,
      align: path,
      alignOrigin: [0.5, 0.5],
      autoRotate: false
    },
    onUpdate: function () {
      // 캐릭터 표정 변화
      const progress = this.progress();
      const face = character.querySelector('.character-face');

      // 진행에 따른 회전
      const rotation = Math.sin(progress * Math.PI * 6) * 15;
      gsap.set(face, {rotation: rotation});

      // 눈 깜빡임
      if (progress > 0.3 && progress < 0.32 || progress > 0.7 && progress
          < 0.72) {
        const eyes = face.querySelectorAll('circle');
        gsap.to(eyes, {scaleY: 0.2, duration: 0.1, yoyo: true, repeat: 1});
      }
    }
  });

  // 별 깜빡임 애니메이션
  const stars = document.querySelectorAll('.star');
  stars.forEach(star => {
    const speed = parseFloat(star.dataset.twinkleSpeed);
    const delay = parseFloat(star.dataset.twinkleDelay);

    gsap.to(star, {
      fillOpacity: 0.1,
      duration: speed,
      repeat: -1,
      yoyo: true,
      delay: delay,
      ease: "sine.inOut"
    });
  });
}

/**
 * 다크/라이트 모드에 따른 애니메이션 색상 업데이트
 */
function updateAnimationColors() {
  const isDarkMode = document.body.getAttribute('data-theme') === 'dark';

  // 그리드 색상
  document.querySelectorAll('.grid-start-color').forEach(el => {
    el.setAttribute('stop-color',
        isDarkMode ? 'rgba(99, 102, 241, 0.1)' : 'rgba(79, 70, 229, 0.1)');
  });

  document.querySelectorAll('.grid-end-color').forEach(el => {
    el.setAttribute('stop-color',
        isDarkMode ? 'rgba(99, 102, 241, 0.05)' : 'rgba(79, 70, 229, 0.05)');
  });

  // 경로 색상
  document.querySelectorAll('.path-start-color').forEach(el => {
    el.setAttribute('stop-color', isDarkMode ? '#6366f1' : '#4f46e5');
  });

  document.querySelectorAll('.path-end-color').forEach(el => {
    el.setAttribute('stop-color', isDarkMode ? '#a78bfa' : '#8b5cf6');
  });

  // 캐릭터 색상
  document.querySelectorAll('.character-body').forEach(el => {
    el.setAttribute('fill', isDarkMode ? '#6366f1' : '#4f46e5');
  });

  document.querySelectorAll('.character-glow').forEach(el => {
    el.setAttribute('fill',
        isDarkMode ? 'rgba(99, 102, 241, 0.3)' : 'rgba(79, 70, 229, 0.3)');
  });

  // 별 색상
  document.querySelectorAll('.star').forEach(el => {
    el.setAttribute('fill', isDarkMode ? '#818cf8' : '#6366f1');
  });

  // 404 텍스트 색상
  document.querySelectorAll('.text-404').forEach(el => {
    el.setAttribute('fill', isDarkMode ? '#ffffff' : '#000000');
  });
}

/**
 * 검색 패널 초기화
 */
function initSearch() {
  const searchBtn = document.getElementById('search-btn');
  const searchPanel = document.getElementById('search-panel');
  const searchInput = document.getElementById('search-input');
  const searchSubmit = document.getElementById('search-submit');

  // 중복으로 이벤트가 바인딩되지 않도록 확인
  if (searchBtn) {
    console.log('검색 버튼 이벤트 등록');
    // 이미 HTML에서 onclick으로 처리하므로 여기서는 주석 처리
    // searchBtn.addEventListener('click', toggleSearchPanel);
  } else {
    console.error('검색 버튼을 찾을 수 없습니다');
  }

  // 검색 실행 함수
  function executeSearch() {
    const query = searchInput.value.trim();
    if (query) {
      console.log('검색 실행:', query);
      window.location.href = '/search?q=' + encodeURIComponent(query);
    }
  }

  // 검색 버튼 클릭 시 검색 실행
  if (searchSubmit) {
    searchSubmit.addEventListener('click', executeSearch);
  }

  // Enter 키 입력 시 검색 실행
  if (searchInput) {
    searchInput.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') {
        executeSearch();
      }
    });
  }
}

// 글로벌 토글 함수 추가 (HTML에서 직접 호출 가능)
function toggleSearchPanel() {
  console.log('toggleSearchPanel 함수 호출됨');
  const searchPanel = document.getElementById('search-panel');
  const searchInput = document.getElementById('search-input');

  if (!searchPanel) {
    console.error('검색 패널 요소를 찾을 수 없습니다');
    return;
  }

  // hidden 클래스 토글
  searchPanel.classList.toggle('hidden');

  // show 클래스 토글
  if (searchPanel.classList.contains('hidden')) {
    searchPanel.classList.remove('show');
  } else {
    searchPanel.classList.add('show');
    searchInput.focus();

    // 팝콘 효과 트리거
    triggerConfetti();
  }
}

/**
 * 팝콘 파티클 효과 초기화
 */
function initConfetti() {
  const canvas = document.getElementById('confetti-canvas');
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  window.addEventListener('resize', () => {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  });
}

/**
 * 팝콘 효과 발생
 */
function triggerConfetti() {
  const canvas = document.getElementById('confetti-canvas');
  const ctx = canvas.getContext('2d');

  const particles = [];
  const particleCount = 50;

  // 파티클 생성
  for (let i = 0; i < particleCount; i++) {
    particles.push({
      x: canvas.width / 2,
      y: canvas.height / 2,
      size: Math.random() * 8 + 3,
      color: getRandomColor(),
      speed: Math.random() * 5 + 2,
      angle: Math.random() * Math.PI * 2,
      spin: Math.random() * 0.2 - 0.1,
      opacity: 1
    });
  }

  // 랜덤 색상 생성
  function getRandomColor() {
    const colors = ['#4f46e5', '#8b5cf6', '#ec4899', '#f43f5e', '#f59e0b',
      '#10b981'];
    return colors[Math.floor(Math.random() * colors.length)];
  }

  // 애니메이션 함수
  function animate() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    let stillActive = false;

    particles.forEach(p => {
      if (p.opacity <= 0) {
        return;
      }

      stillActive = true;

      p.x += Math.cos(p.angle) * p.speed;
      p.y += Math.sin(p.angle) * p.speed + 0.5; // 중력 효과
      p.angle += p.spin;
      p.speed *= 0.99;
      p.opacity -= 0.01;

      ctx.save();
      ctx.translate(p.x, p.y);
      ctx.rotate(p.angle);
      ctx.globalAlpha = p.opacity;
      ctx.fillStyle = p.color;

      // 랜덤 모양 (원, 사각형, 삼각형)
      const shapeType = Math.floor(Math.random() * 3);
      if (shapeType === 0) {
        // 원
        ctx.beginPath();
        ctx.arc(0, 0, p.size, 0, Math.PI * 2);
        ctx.fill();
      } else if (shapeType === 1) {
        // 사각형
        ctx.fillRect(-p.size, -p.size, p.size * 2, p.size * 2);
      } else {
        // 삼각형
        ctx.beginPath();
        ctx.moveTo(0, -p.size);
        ctx.lineTo(p.size, p.size);
        ctx.lineTo(-p.size, p.size);
        ctx.closePath();
        ctx.fill();
      }

      ctx.restore();
    });

    if (stillActive) {
      requestAnimationFrame(animate);
    }
  }

  // 애니메이션 시작
  animate();
}