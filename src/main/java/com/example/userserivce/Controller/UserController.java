package com.example.userserivce.Controller;

import com.example.userserivce.Dto.UserDto;
import com.example.userserivce.jpaRepository.UserEntity;
import com.example.userserivce.service.UserService;
import com.example.userserivce.vo.Greeting;
import com.example.userserivce.vo.RequestUser;
import com.example.userserivce.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    Environment env;
    private UserService userService;

    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/health-check")
    public String status(){
        return String.format("It's working on user-service on PORT"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", token secret=" + env.getProperty("token.secret")
                + ", token expiration-time=" + env.getProperty("token.expiration-time"));
    }

    @GetMapping("/welcome")
    public String welcome(){
        return env.getProperty("greeting.message");
    }

    // 회원가입
    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createuser(@RequestBody RequestUser requestUser){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser); //201번(생성됨)
    }

    // 유저 조회
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> users(){
        Iterable<UserEntity> userList = userService.getUserByAll();

        // 변환
        ModelMapper modelMapper = new ModelMapper();
        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(modelMapper.map(v, ResponseUser.class));
        });


        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 유저 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){
        UserDto userDto = userService.getUserById(userId);
        ModelMapper mapper = new ModelMapper();
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

}
