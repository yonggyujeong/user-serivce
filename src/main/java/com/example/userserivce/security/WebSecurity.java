package com.example.userserivce.security;

import com.example.userserivce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/*
* WebSecurity
* 인증과 권한에 관한 작업들을 설정하는 클래스
* 기존에는 WebSecurityConfigureAdapter의 configure 메서드 두개를
* 각각 인증, 권한에 사용하였으나
* 현재는 depricated 됨*/

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebSecurity {
    private Environment env;
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    @Autowired
    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.env     = env; // 지속시간등
    }

    // 권한 작업 설정
    //@Order(SecurityProperties.BASIC_AUTH_ORDER)
    @Bean   //override 안하는 대신 bean으로 등록해줘야 실행됨
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception{

//        // Configure AuthenticationManagerBuilder
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//
//        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();


        http.csrf().disable();

        // 인증이 된 내역만 혀용
        http.authorizeRequests()                // 요청에 의한 보안검사 시작
//                .anyRequest().permitAll()       // 어떤 요청이든 허용
//                .anyRequest().authenticated()   // 어떤 요청이든 보안검사를 한다.
                .antMatchers("/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                //.access("hasIpAddress('" + "172.30.1.89" + "')")
                .and()
                .addFilter(getAuthenticationFilter(authenticationManager));
//              .formLogin().disable();

        http.headers().frameOptions().disable();
        // http 헤더에 프레임 별로 데이터가 나눠져 잇는걸 사용하지 않음 -> h2 콘솔에서 볼 수 있게

        return http.build();
    }


    protected AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) { // 파라메터로 받음
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager ,userService, env);
        authenticationFilter.setAuthenticationManager(authenticationManager); // authenticationManager 필요

        return authenticationFilter;
    }

    // 인증 작업 설정 -> 결국 userDatailsService가 등록된 AuthenticationManager를 생성, 반환하는 목적
    // select pwd from users where email = ?
    // db_pwd(encrypted) == input_pwd

    // 기존
//    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
//        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
//    }
    //변경
    @Bean   // 해당 클래스명이 등록되면 가능한가? bcrpt강의 참고 -> return클래스 제일 앞을 소문자로 바꿔서 등록
    public AuthenticationManager authenticationManager(HttpSecurity http, UserService userDetailsService) throws Exception{
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);

        return authenticationManagerBuilder.build();
    }


}
