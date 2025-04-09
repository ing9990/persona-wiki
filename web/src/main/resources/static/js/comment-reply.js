document.addEventListener("click", async function (e) {
  const target = e.target.closest(".reply-toggle");
  if (!target) {
    return;
  }

  const commentId = target.dataset.commentId;
  const figureId = target.dataset.figureId;

  const replyContainer = target.closest(".comment").querySelector(
      ".reply-container");
  const repliesList = replyContainer.querySelector(".replies-list");

  // 이미 불러온 경우 toggle
  if (!repliesList.innerHTML.trim()) {
    const res = await fetch(
        `/figures/${figureId}/comments/${commentId}/replies/fragment`);
    const html = await res.text();
    repliesList.innerHTML = html;
  }

  replyContainer.classList.toggle("hidden");
});
