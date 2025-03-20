document.addEventListener('DOMContentLoaded', function () {
    const searchForm = document.getElementById('search-form');
    const searchBar = document.getElementById('search-bar');
    const wordCloudDiv = document.getElementById('word-cloud');
    const sentimentChartCanvas = document.getElementById('sentiment-chart');
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
            fetchData(keyword);
        }
    });

    // 서버에서 데이터를 가져와 페이지에 표시
    function fetchData(keyword) {
        // 워드 클라우드 데이터 가져오기
        fetch('/api/word-cloud?keyword=' + encodeURIComponent(keyword))
            .then(response => response.json())
            .then(data => {
                renderWordCloud(data);
            });

        // 부정/긍정 평가 데이터 가져오기
        fetch('/api/sentiment?keyword=' + encodeURIComponent(keyword))
            .then(response => response.json())
            .then(data => {
                renderSentimentChart(data);
            });

        // 요약 문장 데이터 가져오기
        fetch('/api/summary?keyword=' + encodeURIComponent(keyword))
            .then(response => response.json())
            .then(data => {
                renderSummary(data);
            });
    }

    // 워드 클라우드 렌더링
    function renderWordCloud(words) {
        d3.layout.cloud().size([1000, 700])
            .words(words.map(d => ({ text: d.text, size: d.value * 10 })))
            .padding(8)
            .rotate(() => ~~(Math.random() * 2) * 90)
            .fontSize(d => d.size)
            .on("end", draw)
            .start();

        function draw(words) {
            d3.select("#word-cloud").html("");
            d3.select("#word-cloud").append("svg")
                .attr("width", 1000)
                .attr("height", 700)
                .append("g")
                .attr("transform", "translate(500,350)")
                .selectAll("text")
                .data(words)
                .enter().append("text")
                .style("font-size", d => d.size + "px")
                .style("fill", "#69b3a2")
                .attr("text-anchor", "middle")
                .attr("transform", d => "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")")
                .text(d => d.text);
        }
    }

    // 부정/긍정 평가 차트 렌더링
    function renderSentimentChart(data) {
        const chartData = {
            labels: ['Sentiment'],
            datasets: [
                {
                    label: 'Positive',
                    data: [data.positive],
                    backgroundColor: '#36a2eb'
                },
                {
                    label: 'Negative',
                    data: [data.negative],
                    backgroundColor: '#ff6384'
                }
            ]
        };
        
        new Chart(sentimentChartCanvas, {
            type: 'horizontalBar',  // 가로 바 차트로 변경
            data: chartData,
            options: {
                scales: {
                    xAxes: [{
                        stacked: true,  // 막대그래프 쌓기 설정
                        ticks: {
                            beginAtZero: true,
                            max: 100  // 최대 100%로 설정
                        }
                    }],
                    yAxes: [{
                        stacked: true  // 막대그래프 쌓기 설정
                    }]
                }
            }
        });
    }

    // 요약 문장 렌더링
    function renderSummary(sentences) {
        summaryDiv.innerHTML = sentences.map(sentence => `<p>${sentence}</p>`).join('');
    }
});dl
