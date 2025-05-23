/**
 * 카테고리 섹션 인터랙션 스크립트
 * 세련된 움직임과 호버 효과를 추가합니다.
 */
document.addEventListener('DOMContentLoaded', function () {
  initCategoryHoverEffects();
});

/**
 * 카테고리 섹션 요소들에 인터랙션 효과를 추가합니다.
 */
function initCategoryHoverEffects() {
  // 카테고리 카드에 마우스 이동 효과 추가
  const categoryCards = document.querySelectorAll('.stat-card');

  categoryCards.forEach(card => {
    // 이미지와 오버레이 요소
    const cardImage = card.querySelector('.category-card-image');
    const cardOverlay = card.querySelector('.category-overlay');
    const iconBadge = card.querySelector('.category-icon-badge');

    if (cardImage && cardOverlay) {
      // 시작할 때 랜덤한 위치에서 이미지 조금 확대
      const initialScale = 1.02 + (Math.random() * 0.03);
      const initialX = -2 + (Math.random() * 4); // -2px에서 2px 사이
      const initialY = -2 + (Math.random() * 4); // -2px에서 2px 사이

      cardImage.style.transform = `scale(${initialScale}) translate(${initialX}px, ${initialY}px)`;

      // 마우스 이동에 따른 효과
      card.addEventListener('mousemove', function (e) {
        // 마우스 위치에 따라 이미지 살짝 움직임
        const rect = card.getBoundingClientRect();
        const mouseX = (e.clientX - rect.left) / rect.width;
        const mouseY = (e.clientY - rect.top) / rect.height;

        // 이미지 움직임 (약간의 시차 효과)
        const moveX = (mouseX - 0.5) * 5; // -2.5px ~ 2.5px
        const moveY = (mouseY - 0.5) * 5; // -2.5px ~ 2.5px

        cardImage.style.transform = `scale(1.05) translate(${moveX}px, ${moveY}px)`;

        // 오버레이 불투명도 변화
        const opacity = 0.3 + (mouseY * 0.2); // 0.3 ~ 0.5
        cardOverlay.style.opacity = opacity;

        // 아이콘 배지 움직임
        if (iconBadge) {
          const badgeMoveX = (mouseX - 0.5) * 8;
          const badgeMoveY = (mouseY - 0.5) * 8;
          iconBadge.style.transform = `translate(${badgeMoveX}px, ${badgeMoveY}px) scale(1.1)`;
        }
      });

      // 마우스가 떠났을 때 원래 상태로
      card.addEventListener('mouseleave', function () {
        cardImage.style.transform = `scale(1)`;
        cardOverlay.style.opacity = '';

        if (iconBadge) {
          iconBadge.style.transform = '';
        }

        // 약간의 지연 후 원래 랜덤 위치로 돌아감
        setTimeout(() => {
          const initialScale = 1.02 + (Math.random() * 0.03);
          const initialX = -2 + (Math.random() * 4);
          const initialY = -2 + (Math.random() * 4);

          cardImage.style.transform = `scale(${initialScale}) translate(${initialX}px, ${initialY}px)`;
        }, 300);
      });
    }
  });

  // 링크 호버 효과
  const categoryLinks = document.querySelectorAll('.category-link');
  categoryLinks.forEach(link => {
    link.addEventListener('mouseenter', function () {
      const icon = this.querySelector('i');
      if (icon) {
        icon.style.transform = 'translateX(5px)';
      }
    });

    link.addEventListener('mouseleave', function () {
      const icon = this.querySelector('i');
      if (icon) {
        icon.style.transform = '';
      }
    });
  });
}