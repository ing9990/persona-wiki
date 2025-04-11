(function () {
  window.makeSlug = function (name) {
    return name
    .trim()
    .toLowerCase()
    .replace(/\s+/g, '-')             // 공백 → 하이픈
    .replace(/_/g, '-')              // 언더스코어 → 하이픈
    .replace(/[^a-z0-9가-힣\-]/g, ''); // 특수문자 제거 (한글, 영문, 숫자, 하이픈만 허용)
  };
})();
