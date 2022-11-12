package com.example.userserivce.service;

import com.example.userserivce.Dto.UserDto;
import com.example.userserivce.client.OrderServiceClient;
import com.example.userserivce.jpaRepository.UserEntity;
import com.example.userserivce.jpaRepository.UserRepository;
import com.example.userserivce.vo.ResponseOrder;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;
    Environment env;
    RestTemplate restTemplate;

    OrderServiceClient orderServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if(userEntity == null){
            throw new UsernameNotFoundException(username);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPw(), true, true,
                true, true,
                new ArrayList<>()); //로그인이 되었을때, 권한을 추가하는 작업(리스트)
    }

    @Autowired //생성자 주입이 더 좋음
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder
            , Environment env, RestTemplate restTemplate, OrderServiceClient orderServiceClient) {
        //Bcrypt-> 빈으로 등록해 줘야함(가장 먼저 빈으로 등록되는 곳에 - springbootaplication)
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
        this.orderServiceClient = orderServiceClient;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //강력한 일치만 매핑
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPw(passwordEncoder.encode(userDto.getPw()));
        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    @Override
    public UserDto getUserById(String userId) {
        // 유저 정보
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null){
            throw new UsernameNotFoundException("user not found"); //원래는 로그인에 쓰임.,에러메세지 생성시켜도됨
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);


        /* Using RestTemplate */
//        //주문정보 -> 나중에 orderservice로 부터 받아올것
//        List<ResponseOrder> orders = new ArrayList<>();
//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET  //url, methodType, params, returnType
//                , null, new ParameterizedTypeReference<List<ResponseOrder>>() {
//        });
//        //ResponseEntity 에서 내용물 꺼내기
//        List<ResponseOrder> ordersList = orderListResponse.getBody();


        /* Using Feign Client */
        /* Feign Exception Handling */ // error decoder 사용으로 인한 주석
//        List<ResponseOrder> ordersList = null;
//        try{
//            ordersList = orderServiceClient.getOrders(userId);
//        }catch (FeignException e){
//            log.error(e.getMessage());
//        }
        List<ResponseOrder> ordersList = orderServiceClient.getOrders(userId);




        userDto.setOrders(ordersList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userEntity, UserDto.class);
        if(userDto == null){
            throw new UsernameNotFoundException(email); // 정보가 없습니다.
        }
        return userDto;
    }
}
