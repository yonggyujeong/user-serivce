package com.example.userserivce;

import com.example.userserivce.error.FeignErrorDecoder;
import feign.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class UserSerivceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserSerivceApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}


	@Bean
	//@LoadBalanced
	public RestTemplate getRestTemplate(){ // 빈 등록용
		return new RestTemplate();
	}

	@Bean	// feign 로깅
	public Logger.Level feignLoggerLevel(){
		return Logger.Level.FULL;
	}

	@Bean
	public FeignErrorDecoder feignErrorDecoder(){
		return new FeignErrorDecoder();
	}

}
