package com.example.simplewebserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.simplewebserver.data.Create_db;
import com.example.simplewebserver.service.KeywordService;
import com.example.simplewebserver.service.project2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



@Controller
public class RootController {
	private static final Logger logger = LoggerFactory.getLogger(DataController.class);	 
	 
    // "/"로 접속하면 "/main.html"로 리디렉션
    @GetMapping("/")
    public String redirectToMain() {
        return "redirect:/main.html";  // 오직 루트 경로로 들어올 때만 리디렉트
    }

    // main.html 템플릿을 반환
    @GetMapping("/main.html")	
    public String mainPage() {
        return "main";  // main.html 페이지 템플릿 반환
    }
    
}
