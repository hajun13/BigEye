package com.example.simplewebserver.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.*;
import java.net.Socket;

@Service
public class KeywordService {

    @Async
    public void processKeyword(String keyword) {
        // 키워드를 처리하는 로직을 여기에 작성합니다.
//        System.out.println("Processing keyword: " + keyword);
//        
//        try (Socket socket = new Socket("localhost", 5000)){
//            // 예시: project2 객체 생성 및 작업 수행
//        	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            
//            
//          
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}