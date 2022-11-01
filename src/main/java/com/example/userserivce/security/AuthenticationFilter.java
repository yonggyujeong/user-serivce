package com.example.userserivce.security;

import com.example.userserivce.Dto.UserDto;
import com.example.userserivce.service.UserService;
import com.example.userserivce.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;
    private Environment env;

    private AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager, // 생성자로 받도록 변경
                                UserService userService,
                                Environment env) { // WebSecurity에서 받아씀
        this.userService = userService;
        this.env = env;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            // 인풋 스트림 사용하는 이유
            // post의 body에 담겨오는 데이터는 Request Parameter로 받을 수 없음
            // InputStream으로 처리하면 이를 수동으로 처리할 수 있음
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            // 인증 처리해 주는 매니저
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken( // ID PW를 spring security에서 사용하는 형식으로 변환
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );


        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // 성공 후 이루어 지는 작업들
        // 토큰 부여
        // 토큰 만료 시간 등
        // userEmail로 userId(UUID)를 가져와서 JWT 토큰을 만든다.
        String username = ((User)authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(username);

        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration-time")))) // 현재시간 + 유효기간
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());

    }

}
