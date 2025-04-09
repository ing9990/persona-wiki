/**
 * 스크롤 상단 이동 버튼
 * 스크롤이 일정 거리 이상 내려가면 버튼을 표시하고, 클릭 시 페이지 상단으로 스크롤합니다.
 */
document.addEventListener('DOMContentLoaded', function() {
  // 버튼 생성
  const scrollTopButton = document.createElement('button');
  scrollTopButton.className = 'scroll-to-top-btn hidden';
  scrollTopButton.innerHTML = '<i class="fas fa-arrow-up"></i>';
  document.body.appendChild(scrollTopButton);

  // 스크롤 이벤트 처리
  window.addEventListener('scroll', function() {
    // 300px 이상 스크롤 시 버튼 표시
    if (window.scrollY > 300) {
      scrollTopButton.classList.remove('hidden');
    } else {
      scrollTopButton.classList.add('hidden');
    }
  });

  // 버튼 클릭 이벤트
  scrollTopButton.addEventListener('click', function() {
    // 부드러운 스크롤로 상단 이동
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  });
});