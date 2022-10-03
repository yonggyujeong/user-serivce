package com.example.userserivce.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data // setter getter
public class Greeting {
    @Value("${greeting.message}") //lombok X, springboot Annotiaton O
    private String message;

}
