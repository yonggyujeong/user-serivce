package com.example.userserivce.jpaRepository;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class UserEntity { // DB에 저장될 정보들
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동생성
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, unique = true)
    private String encryptedPw;

}
