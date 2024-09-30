package com.example.simplewebserver.data;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class Create_db {
	
    // My slq 연결
    private Connection connect() { //내부함수
        String url = "jdbc:mysql://localhost:3306/sum";
        String user = "root";
        String password = "****";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());        }
        return conn;
    }
    
    public void wgptinsert(String word, String content) { // 
        String sql = "INSERT INTO wordmsg(word, content) VALUES(?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word);
            pstmt.setString(2, content);
            pstmt.executeUpdate(); 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void WUinsert(String word, String url) { // gptdata에 자료 추가
        String sql = "INSERT INTO wordUrl(word, url) VALUES(?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word);
            pstmt.setString(2, url);
            pstmt.executeUpdate(); 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    public void sentiInsert(String word, String posi, String nega) {
    	String sql = "INSERT INTO wordsenti(word, posi, nega) VALUES(?, ?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word);
            pstmt.setString(2, posi);
            pstmt.setString(3, nega);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void wordinsert(String word, String keyword, String score) { // keydata에 자료 입력
        String sql = "INSERT INTO wordTable(word, keyword, score) VALUES(?, ?, ?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word);
            pstmt.setString(2, keyword);
            pstmt.setString(3, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String wordselJson(String word) {
        String sql = "SELECT keyword, score FROM wordtable where word = ?";
        JSONArray jsonArray = new JSONArray();

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, word);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String keyword = rs.getString("keyword");
                    Double score = rs.getDouble("score");
                    score = score * 1000;

                    // Create a JSON object for each row
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("text", keyword);
                    jsonObject.put("value", score);

                    // Add the JSON object to the JSON array
                    jsonArray.add(jsonObject);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        // Convert the JSON array to a JSON string
        return jsonArray.toJSONString();
    }
    
    

    
    public String sentimentJson(String keyword) {
        String sql = "SELECT posi, nega FROM wordsenti where word = ?";
        JSONObject jsonObject = new JSONObject();

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, keyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int positive = rs.getInt("posi");
                    int negative = rs.getInt("nega");

                    // JSON 객체를 생성하여 데이터 추가
                    jsonObject.put("positive", positive);
                    jsonObject.put("negative", negative);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "{}"; // 오류 발생 시 빈 JSON 객체 반환
        }

        // JSON 객체를 문자열로 변환하여 반환
        return jsonObject.toJSONString();
    }
    
    public String getSummaryJson(String word) {
        String sql = "SELECT content FROM wordmsg where word = ?";
        JSONArray jsonArray = new JSONArray();

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, word);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String summary = rs.getString("content");

                    // JSON 객체 생성
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("summary", summary);

                    // JSON 배열에 추가
                    jsonArray.add(jsonObject);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "[]";  // 빈 JSON 배열 반환
        }

        // JSON 배열을 문자열로 변환
        return jsonArray.toJSONString();
    }
    


    public boolean isWord(String word) { // url 이 있는지 gptdata에서 확인
    	String sql = "SELECT word From wordmsg where word = ?";
    	Boolean satus = false;
		try (Connection conn = this.connect();
			PreparedStatement pstmt = conn.prepareStatement(sql);){
			pstmt.setString(1, word);
			try(ResultSet rs = pstmt.executeQuery()){
				while(rs.next()) {
					String str = rs.getString("word");
					if(word.equals(str)) {
						satus =  true;
					}
				}
			}
			}catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		return satus;
    }
    
}
