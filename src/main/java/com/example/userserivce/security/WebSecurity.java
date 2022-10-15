package com.example.userserivce.security;

import com.example.userserivce.service.UserService;
import com.netflix.discovery.converters.Auto;
import org.apache.catalina.startup.WebAnnotationSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.Filter;

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
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();

        // 인증이 된 내역만 혀용
//        http.authorizeRequests().antMatchers("/**")
//                        .hasIpAddress("192.168.200.103")
//                            .and()
//                            .addFilter(getAuthenticationFilter());





        http.headers().frameOptions().disable();
        // http 헤더에 프레임 별로 데이터가 나눠져 잇는걸 사용하지 않음 -> h2 콘솔에서 볼 수 있게

        return http.build();
    }



    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) { // 파라메터로 받음
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager); // authenticationManager 필요

        return authenticationFilter;
    }

    // 권한 작업 설정
    // select pwd from users where email = ?
    // db_pwd(encrypted) == input_pwd
    private void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }


}
