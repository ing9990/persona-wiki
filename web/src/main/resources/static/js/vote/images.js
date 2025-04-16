// 애니메이션용 이미지 동적 로딩 스크립트
document.addEventListener('DOMContentLoaded', function () {
  // 애니메이션에 사용될 이미지 미리 로드
  preloadAnimationImages();
});

/**
 * 애니메이션에 필요한 이미지를 미리 로드하는 함수
 */
function preloadAnimationImages() {
  const imagesToPreload = [
    '/img/light-rays.svg',
    '/img/angel-wings.svg',
    '/img/flames.svg',
    '/img/prison-bars.svg'
  ];

  imagesToPreload.forEach(src => {
    const img = new Image();
    img.src = src;
  });
}