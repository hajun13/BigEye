package com.example.simplewebserver.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.simplewebserver.service.project2;

@Service
public class asyncProcess {
	
    @Async
    public CompletableFuture<Void> handleKeyword(String keyword) {
        // 키워드 처리 로직
    	return CompletableFuture.runAsync(() -> {
            System.out.println("Processing keyword: " + keyword);

            // 파이썬 서버와 통신하여 데이터 전송 및 수신
            try (Socket socket = new Socket("localhost", 9999); // 파이썬 서버의 주소와 포트
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                project2 pj = new project2(keyword, socket);
                
                // 가장 오래 걸리는 작업인 kewordPro 메서드 실행
                pj.kewordPro();

                System.out.println("끝");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });       
    }
}
