package com.example.simplewebserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.simplewebserver")
public class SimplewebserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimplewebserverApplication.class, args);
	}

}
