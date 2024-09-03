package com.example.simplewebserver.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://54.180.237.142:8081/")
public class HelloController {

    @GetMapping("/")
    public String sayHello() {
        return "Hello, World!";
    }
}

