function search(event) {
    event.preventDefault(); // 폼 제출 기본 동작 방지

    // 검색어 입력 값을 가져옵니다.
    var query = document.getElementById('searchInput').value;

    if (query) {
        fetch('http://localhost:8081/api/submit', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({ 'keyword': query })
        })
        .then(response => {
            // 서버 응답을 처리하고 리디렉션
            // 여기서 response.json() 또는 response.text()를 사용할 수 있습니다.
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text(); // 응답이 텍스트인 경우
        })
        .then(data => {
            console.log('Server response:', data);
            // 서버 응답을 받고 리디렉션
            window.location.href = 'result_page.html?keyword=' + encodeURIComponent(query);
        })
        .catch(error => {
            console.error('Error:', error);
        });
    }
}

document.getElementById('searchForm').addEventListener('submit', search);
