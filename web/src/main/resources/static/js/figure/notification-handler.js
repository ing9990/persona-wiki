// web/src/main/resources/static/js/figure/notification-handler.js

/**
 * 알림 기능을 처리하는 핸들러
 */
class NotificationHandler {
  constructor() {
    this.initializeElements();
    this.initializeNotifications();
    this.setupEventListeners();
  }

  /**
   * DOM 요소 초기화
   */
  initializeElements() {
    // 알림 아이콘/버튼 찾기
    this.notificationButton = document.getElementById('notification-btn');

    // 알림 컨테이너 생성 (없으면)
    if (!document.getElementById('notification-container')) {
      const container = document.createElement('div');
      container.id = 'notification-container';
      container.className = 'fixed top-16 right-4 bg-white rounded-lg shadow-lg z-50 w-80 max-h-96 overflow-y-auto hidden';
      document.body.appendChild(container);

      // 헤더 추가
      const header = document.createElement('div');
      header.className = 'p-3 border-b border-gray-200 flex justify-between items-center';
      header.innerHTML = `
        <h3 class="font-medium text-gray-800">알림</h3>
        <button id="mark-all-read-btn" class="text-xs text-indigo-600 hover:text-indigo-800">
          모두 읽음 표시
        </button>
      `;
      container.appendChild(header);

      // 알림 목록 컨테이너 추가
      const list = document.createElement('div');
      list.id = 'notification-list';
      list.className = 'py-2';
      container.appendChild(list);

      // 푸터 추가
      const footer = document.createElement('div');
      footer.className = 'p-3 border-t border-gray-200 text-center';
      footer.innerHTML = `
        <a href="#" class="text-sm text-indigo-600 hover:text-indigo-800">
          모든 알림 보기
        </a>
      `;
      container.appendChild(footer);

      this.notificationContainer = container;
      this.notificationList = list;
      this.markAllReadButton = document.getElementById('mark-all-read-btn');
    }
  }

  /**
   * 알림 초기화 (가상 데이터)
   */
  initializeNotifications() {
    // 실제로는 API 호출하여 알림 데이터 가져와야 함
    this.notifications = [
      {
        id: 1,
        type: 'reply',
        content: '익명님이 회원님의 댓글에 답글을 달았습니다.',
        commentId: 123,
        createdAt: new Date(Date.now() - 3600000).toISOString(), // 1시간 전
        read: false
      },
      {
        id: 2,
        type: 'mention',
        content: '익명님이 회원님을 멘션했습니다.',
        commentId: 456,
        createdAt: new Date(Date.now() - 86400000).toISOString(), // 1일 전
        read: false
      }
    ];

    // 알림 UI 업데이트
    this.updateNotificationUI();
  }

  /**
   * 이벤트 리스너 설정
   */
  setupEventListeners() {
    // 알림 버튼 클릭
    if (this.notificationButton) {
      this.notificationButton.addEventListener('click',
          this.toggleNotifications.bind(this));
    }

    // 모두 읽음 표시 버튼 클릭
    if (this.markAllReadButton) {
      this.markAllReadButton.addEventListener('click',
          this.markAllAsRead.bind(this));
    }

    // 문서 클릭 시 알림 패널 닫기
    document.addEventListener('click', (e) => {
      if (this.notificationContainer &&
          !this.notificationContainer.contains(e.target) &&
          e.target !== this.notificationButton) {
        this.notificationContainer.classList.add('hidden');
      }
    });
  }

  /**
   * 알림 UI 업데이트
   */
  updateNotificationUI() {
    // 읽지 않은 알림 수 계산
    const unreadCount = this.notifications.filter(n => !n.read).length;

    // 알림 버튼에 배지 표시
    if (this.notificationButton) {
      // 기존 배지 제거
      const existingBadge = this.notificationButton.querySelector(
          '.notification-badge');
      if (existingBadge) {
        existingBadge.remove();
      }

      // 읽지 않은 알림이 있으면 배지 추가
      if (unreadCount > 0) {
        const badge = document.createElement('span');
        badge.className = 'notification-badge';
        badge.textContent = unreadCount > 9 ? '9+' : unreadCount;
        this.notificationButton.appendChild(badge);
      }
    }

    // 알림 목록 업데이트
    if (this.notificationList) {
      this.notificationList.innerHTML = '';

      if (this.notifications.length === 0) {
        // 알림이 없는 경우
        const emptyMessage = document.createElement('div');
        emptyMessage.className = 'p-4 text-center text-gray-500';
        emptyMessage.textContent = '알림이 없습니다.';
        this.notificationList.appendChild(emptyMessage);
      } else {
        // 알림 항목 추가
        this.notifications.forEach(notification => {
          const item = document.createElement('div');
          item.className = `p-3 border-b border-gray-100 hover:bg-gray-50 cursor-pointer ${notification.read
              ? 'opacity-70' : ''}`;
          item.dataset.id = notification.id;

          // 알림 아이콘 결정
          let icon = '';
          if (notification.type === 'reply') {
            icon = '<i class="fas fa-reply text-indigo-500 mr-2"></i>';
          } else if (notification.type === 'mention') {
            icon = '<i class="fas fa-at text-indigo-500 mr-2"></i>';
          }

          // 시간 포맷팅
          const timeAgo = this.timeAgo(new Date(notification.createdAt));

          item.innerHTML = `
            <div class="flex items-start">
              ${notification.read ? ''
              : '<span class="w-2 h-2 mt-1 mr-2 bg-indigo-600 rounded-full"></span>'}
              <div class="flex-1">
                <div class="flex items-center mb-1">
                  ${icon}
                  <span class="text-sm">${notification.content}</span>
                </div>
                <div class="text-xs text-gray-500">${timeAgo}</div>
              </div>
            </div>
          `;

          // 알림 클릭 이벤트
          item.addEventListener('click',
              () => this.handleNotificationClick(notification));

          this.notificationList.appendChild(item);
        });
      }
    }
  }

  /**
   * 알림 클릭 처리
   * @param {Object} notification - 알림 객체
   */
  handleNotificationClick(notification) {
    // 알림 읽음 처리
    this.markAsRead(notification.id);

    // 해당 댓글로 이동
    if (notification.commentId) {
      const commentElement = document.getElementById(
          `comment-${notification.commentId}`);
      if (commentElement) {
        // 알림 패널 닫기
        this.notificationContainer.classList.add('hidden');

        // 해당 댓글로 스크롤
        commentElement.scrollIntoView({behavior: 'smooth'});

        // 잠시 강조 효과
        commentElement.classList.add('bg-yellow-50');
        setTimeout(() => {
          commentElement.classList.remove('bg-yellow-50');
        }, 3000);
      }
    }
  }

  /**
   * 알림 패널 토글
   */
  toggleNotifications() {
    this.notificationContainer.classList.toggle('hidden');
  }

  /**
   * 알림 읽음 처리
   * @param {number} id - 알림 ID
   */
  markAsRead(id) {
    // 실제로는 API 호출하여 서버에도 상태 업데이트
    this.notifications = this.notifications.map(notification => {
      if (notification.id === id) {
        return {...notification, read: true};
      }
      return notification;
    });

    // UI 업데이트
    this.updateNotificationUI();
  }

  /**
   * 모든 알림 읽음 처리
   */
  markAllAsRead() {
    // 실제로는 API 호출하여 서버에도 상태 업데이트
    this.notifications = this.notifications.map(notification => {
      return {...notification, read: true};
    });

    // UI 업데이트
    this.updateNotificationUI();
  }

  /**
   * 시간 표시 (예: "3분 전")
   * @param {Date} date - 날짜
   * @returns {string} - 시간 표시 문자열
   */
  timeAgo(date) {
    const seconds = Math.floor((new Date() - date) / 1000);

    let interval = Math.floor(seconds / 31536000);
    if (interval > 1) {
      return `${interval}년 전`;
    }

    interval = Math.floor(seconds / 2592000);
    if (interval > 1) {
      return `${interval}개월 전`;
    }

    interval = Math.floor(seconds / 86400);
    if (interval > 1) {
      return `${interval}일 전`;
    }

    interval = Math.floor(seconds / 3600);
    if (interval > 1) {
      return `${interval}시간 전`;
    }

    interval = Math.floor(seconds / 60);
    if (interval > 1) {
      return `${interval}분 전`;
    }

    return '방금 전';
  }

  /**
   * 새 알림 추가
   * @param {Object} notification - 알림 객체
   */
  addNotification(notification) {
    this.notifications.unshift(notification);
    this.updateNotificationUI();
  }
}