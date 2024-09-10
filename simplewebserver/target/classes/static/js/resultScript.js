document.addEventListener('DOMContentLoaded', function () {
    const searchForm = document.getElementById('search-form');
    const searchBar = document.getElementById('search-bar');
    const wordCloudDiv = document.getElementById('wordcloudContainer');
    const sentimentChartCanvas = document.getElementById('sentimentChart');
    const summaryDiv = document.getElementById('summary');

    // 초기 키워드를 URL에서 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const initialKeyword = urlParams.get('keyword');
    if (initialKeyword) {
        searchBar.value = initialKeyword;
        fetchData(initialKeyword);
    }

    // 검색 폼 제출 시 처리
    searchForm.addEventListener('submit', function (e) {
    	e.preventDefault();
    
        const keyword = searchBar.value;
        if (keyword) {
             fetch('http://localhost:8081/api/submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({ 'keyword': keyword })
            })
            .then(() => {
                // 폼 제출 후 서버에서 데이터가 처리되는지 확인
                fetchData(keyword);
            })
            .catch(error => {
                console.error('Error:', error);
            });
        }
    });

    // 서버에서 데이터를 가져와 페이지에 표시
	    
	function fetchData(keyword) {
	    // 폴링을 위한 재귀 함수
	    showLoading();
	    
	    function pollForCompletion() {
	        fetch('http://localhost:8081/api/status?keyword=' + encodeURIComponent(keyword))
	            .then(response => response.text())
	            .then(status => {
	            
	             	console.log('Status:', status);
	             	
	                if (status === 'completed') {
		                hideLoading();
	                    // 데이터 처리 완료 후 실제 데이터 요청
	                    fetch('/api/word-cloud?keyword=' + encodeURIComponent(keyword))
	                        .then(response => response.json())
	                        .then(data => {9
	                            renderWordCloud(data);
	                        });
	
	                    fetch('/api/sentiment?keyword=' + encodeURIComponent(keyword))
	                        .then(response => response.json())
	                        .then(data => {
	                            renderSentimentChart(data);
	                        });
	
	                    fetch('/api/summary?keyword=' + encodeURIComponent(keyword))
	                        .then(response => response.json())
	                        .then(data => {
	                            renderSummary(data);
	                        });
	                } else {
	                    // 상태가 'processing'일 경우, 잠시 후 다시 폴링
	                    setTimeout(pollForCompletion, 3000); // 3초 후 다시 확인
	                }
	            })
	            .catch(error => {
	                console.error('Error fetching status:', error);
	                alert('데이터를 가져오는 중 오류가 발생했습니다. 다시 시도해 주세요.');
	                // 에러 처리
	            });
	    }
	
	    // 폴링 시작
	    pollForCompletion();
	}


    // 워드 클라우드 렌더링
    function renderWordCloud(words) {
    	if (!Array.isArray(words) || words.length === 0) {
        	console.error("Invalid word cloud data format");
        	return;
    	}
    	
    	const maxSize = 120;  // 단어 크기의 최대 값
    	const minSize = 40;
    	const maxValue = Math.max(...words.map(d => d.value));
    
    	const scaledWords = words.map(d => {
            const size = Math.min(maxSize, Math.max(minSize, (d.value / maxValue) * maxSize)); // 값에 비례해 크기 조정
    		return [d.text, size];

    	});
     	
     	WordCloud(document.getElementById('wordcloudContainer'), {
	        list: scaledWords,
	        gridSize: 8,
	        weightFactor: 1,
	        color: function(word, weight) {
                // 어두운 색상 배열을 사용하여 색상을 랜덤으로 선택
                const darkColors = ['#4B0082', '#483C32', '#800000', '#FF4500', '#003366']; // 남색, 갈색, 주황색, 보라색, 남색
                return darkColors[Math.floor(Math.random() * darkColors.length)];
            },
	        rotateRatio: 0.1,
	        backgroundColor: '#fff',
	        drawOutOfBound: false,
	        shape: 'circle',
	        shuffle: true, // 단어를 랜덤으로 배치
	        minRotation: -Math.PI / 36,
	        maxRotation: Math.PI / 36,
	        rotateRatio: 0.1,  // 회전 비율 조정
	        fontFamily: 'Times, serif',
	        click: function (item) {
	            console.log('Clicked:', item[0]);
	        }
	    });
    }

	let sentimentChart
	
    // 부정/긍정 평가 차트 렌더링
    function renderSentimentChart(data) {
    
        if (sentimentChart) {
        	sentimentChart.destroy();
    	}
    
        const chartData = {
	        labels: ['Positive', 'Negative'],
	        datasets: [
	            {
	                data: [data.positive, data.negative],
	                backgroundColor: ['#36a2eb', '#ff6384']
	            }
        	]
    	};
        
        new Chart(sentimentChartCanvas, {
	        type: 'pie',  // 원형 차트로 변경
	        data: chartData,
	        options: {
	            responsive: true,
	            //maintainAspectRatio: false,
	            plugins: {
	                legend: {
	                    position: 'top',  // 범례 위치
	                },
	                tooltip: {
	                    callbacks: {
	                        label: function(tooltipItem) {
	                            const label = tooltipItem.label || '';
	                            if (label) {
	                                return `${label}: ${tooltipItem.raw}%`;  // 데이터의 비율을 보여줍니다
	                            }
	                            return label;
	                        }
	                    }
	                }
	            }
	        }
	    });
    }

    // 요약 문장 렌더링
	function renderSummary(sentences) {
	    if (sentences.length > 0 && sentences[0].summary) {
	        summaryDiv.innerHTML = sentences[0].summary; // 첫 번째 객체의 summary 필드를 HTML로 렌더링
	    } else {
	        console.error("Summary data is not in the expected format");
	    }
	}
	
	function showLoading() {
	    const loadingOverlay = document.getElementById('loadingOverlay');
	    loadingOverlay.classList.remove('hidden');
	}

// 데이터 로딩이 완료되면 로딩 애니메이션 숨기기
	function hideLoading() {
	    const loadingOverlay = document.getElementById('loadingOverlay');
	    loadingOverlay.classList.add('hidden');
	}
});
