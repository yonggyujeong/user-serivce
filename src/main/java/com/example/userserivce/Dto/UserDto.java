package com.example.userserivce.Dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto { //중간단계 클래스
    private String email;
    private String name;
    private String pw;
    private String userId;
    private Date createdAt; //util package Date

    private String encryptedPw;
}
