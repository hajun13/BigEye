function search() {
    // 검색어 입력 값을 가져옵니다.
    var query = document.getElementById('searchInput').value;
    // 검색어가 비어있지 않은 경우에만 결과 페이지로 이동합니다.
    if (query) {
        window.location.href = 'result_page.html?keyword=' + encodeURIComponent(query);
    }
}