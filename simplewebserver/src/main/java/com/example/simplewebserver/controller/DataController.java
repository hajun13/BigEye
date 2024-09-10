package com.example.simplewebserver.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.simplewebserver.data.Create_db;
import com.example.simplewebserver.service.project2;

@RequestMapping("/api")
@RestController
public class DataController {
	
	private final Create_db createDb;
	private final asyncProcess asyncService;
	private static final Logger logger = LoggerFactory.getLogger(DataController.class);
    
	
	@Autowired
    public DataController(Create_db createDb, asyncProcess asyncService) {
        
    	this.createDb = createDb;
    	this.asyncService = asyncService;
    }	
    
    private final ConcurrentMap<String, String> taskStatus = new ConcurrentHashMap<>();

    @PostMapping("/submit")
    public ResponseEntity<String> submitKeyword(@RequestParam("keyword") String keyword) {

    	CompletableFuture<Void> future = asyncService.handleKeyword(keyword);
    	
    	future.thenRun(() -> {
            logger.info("Processing completed for keyword: {}", keyword);

            taskStatus.put(keyword, "completed");
        }).exceptionally(ex -> {
            logger.error("Error processing keyword: {}", keyword, ex);
            taskStatus.put(keyword, "failed"); // 오류 발생 시 상태 업데이트
            return null;
        });
    	
        // 데이터 처리를 비동기적으로 실행
        
        taskStatus.putIfAbsent(keyword, "processing");
        
        return ResponseEntity.ok("Processing started"); // 적절한 응답 반환
    }
  
    
    
    @GetMapping("/status")
    public ResponseEntity<String> getStatus(@RequestParam("keyword") String keyword) {
    	String status = taskStatus.getOrDefault(keyword, "processing");
    	logger.info("Status check for keyword: {}", keyword); 
        return  ResponseEntity.ok(status);
    }
    
    @GetMapping("/word-cloud")
    //wordtable
    public ResponseEntity<String> getWordCloudData(@RequestParam("keyword") String keyword) {
        logger.info("Fetching word cloud data for keyword: {}", keyword);
        try {
            String wordCloudData = createDb.wordselJson(keyword);
            System.out.println(wordCloudData);
            return ResponseEntity.ok(wordCloudData);
        } catch (Exception e) {
            logger.error("Error fetching word cloud data for keyword: {}", keyword, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error fetching word cloud data");
        }
    }
    //wordsenti
    @GetMapping("/sentiment")
    public ResponseEntity<String> getSentimentData(@RequestParam("keyword") String keyword) {
        logger.info("Fetching sentiment data for keyword: {}", keyword);
        try {
            String sentimentData = createDb.sentimentJson(keyword);
            return ResponseEntity.ok(sentimentData);
        } catch (Exception e) {
            logger.error("Error fetching sentiment data for keyword: {}", keyword, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error fetching sentiment data");
        }
    }
    //wordmsg
    @GetMapping("/summary")
    public ResponseEntity<String> getSummaryData(@RequestParam("keyword") String keyword) {
        logger.info("Fetching summary data for keyword: {}", keyword);
        try {
            String summaryData = createDb.getSummaryJson(keyword);
            System.out.println(summaryData);
            return ResponseEntity.ok(summaryData);
        } catch (Exception e) {
            logger.error("Error fetching summary data for keyword: {}", keyword, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error fetching summary data");
        }
    }
    
    
}