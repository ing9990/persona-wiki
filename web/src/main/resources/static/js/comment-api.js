/**
 * 댓글 API 요청 모듈
 * 댓글 관련 API 호출 기능을 제공합니다.
 */
class CommentAPI {
  /**
   * 댓글 목록 가져오기
   * @param {string} figureId - 피규어 ID
   * @param {number} page - 페이지 번호
   * @param {number} size - 페이지 크기
   * @returns {Promise<Object>} - 댓글 데이터
   */
  static async fetchComments(figureId, page, size) {
    try {
      const response = await fetch(
          `/api/v1/figures/${figureId}/comments?page=${page}&size=${size}`
      );

      if (!response.ok) {
        throw new Error("네트워크 에러");
      }

      return await response.json();
    } catch (error) {
      console.error("댓글 로딩 실패:", error);
      throw error;
    }
  }

  /**
   * 댓글에 대한 답글 목록 가져오기
   * @param {string} figureId - 피규어 ID
   * @param {string} commentId - 댓글 ID
   * @returns {Promise<Array>} - 답글 데이터 배열
   */
  static async fetchReplies(figureId, commentId) {
    try {
      const response = await fetch(
          `/api/v1/figures/${figureId}/comments/${commentId}/replies`,
          {method: "GET"}
      );

      if (!response.ok) {
        throw new Error("답글 요청 실패");
      }

      return await response.json();
    } catch (error) {
      console.error("답글 로딩 실패:", error);
      throw error;
    }
  }

  /**
   * 댓글에 대한 답글 목록을 Fragment로 가져오기
   * @param {string} figureId - 피규어 ID
   * @param {string} commentId - 댓글 ID
   * @returns {Promise<string>} - HTML Fragment 문자열
   */
  static async fetchRepliesFragment(figureId, commentId) {
    try {
      const response = await fetch(
          `/figures/${figureId}/comments/fragment/${commentId}/replies`,
          {method: "GET"}
      );

      if (!response.ok) {
        throw new Error("답글 요청 실패");
      }

      return await response.text(); // HTML Fragment를 텍스트로 받음
    } catch (error) {
      console.error("답글 로딩 실패:", error);
      throw error;
    }
  }

  /**
   * 답글 작성하기
   * @param {string} figureId - 피규어 ID
   * @param {string} commentId - 댓글 ID
   * @param {string} content - 답글 내용
   * @returns {Promise<Object>} - 새로 생성된 답글 데이터
   */
  static async createReply(figureId, commentId, content) {
    try {
      const response = await fetch(
          `/api/v1/figures/${figureId}/comments/${commentId}/replies`,
          {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({content})
          }
      );

      if (!response.ok) {
        throw new Error("답글 작성 실패");
      }

      return await response.json();
    } catch (error) {
      console.error("답글 작성 실패:", error);
      throw error;
    }
  }

  /**
   * 답글 작성하기 (Fragment 반환)
   * @param {string} figureId - 피규어 ID
   * @param {string} commentId - 댓글 ID
   * @param {string} content - 답글 내용
   * @returns {Promise<string>} - HTML Fragment 문자열
   */
  static async createReplyFragment(figureId, commentId, content) {
    try {
      const formData = new FormData();
      formData.append('content', content);

      const response = await fetch(
          `/figures/${figureId}/comments/fragment/${commentId}/replies`,
          {
            method: "POST",
            body: formData
          }
      );

      if (!response.ok) {
        throw new Error("답글 작성 실패");
      }

      return await response.text(); // HTML Fragment를 텍스트로 받음
    } catch (error) {
      console.error("답글 작성 실패:", error);
      throw error;
    }
  }

  /**
   * 좋아요/싫어요 토글
   * @param {string} figureId - 피규어 ID
   * @param {string} commentId - 댓글 ID
   * @returns {Promise<Object>} - 응답 데이터
   */
  static async toggleLikeDislike(figureId, commentId) {
    try {
      const url = `/api/v1/figures/${figureId}/comments/${commentId}/toggle`;
      const response = await fetch(url, {
        method: "POST",
        headers: {"Content-Type": "application/json"}
      });

      if (!(response.ok || response.status === 204)) {
        throw new Error("요청 실패");
      }
    } catch (error) {
      console.error("좋아요/싫어요 처리 실패:", error);
      throw error;
    }
  }

  /**
   * 댓글 생성하기
   * @param {string} url - 댓글 생성 API URL
   * @param {string} content - 댓글 내용
   * @returns {Promise<Object>} - 생성된 댓글 데이터
   */
  static async createComment(url, content) {
    try {
      const response = await fetch(url, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({content})
      });

      if (!(response.ok || response.status === 204)) {
        throw new Error("댓글 등록 실패");
      }

      return response.status === 204 ? {} : await response.json();
    } catch (error) {
      console.error("댓글 등록 중 오류:", error);
      throw error;
    }
  }
}

// 전역으로 노출
window.CommentAPI = CommentAPI;