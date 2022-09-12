package com.example.userserivce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UserSerivceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserSerivceApplication.class, args);
	}

}
