package com.example.simplewebserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.simplewebserver")
@Configuration
@EnableAsync
public class SimplewebserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimplewebserverApplication.class, args);
	}
}
