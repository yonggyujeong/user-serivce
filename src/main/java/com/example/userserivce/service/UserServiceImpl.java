package com.example.userserivce.service;

import com.example.userserivce.Dto.UserDto;
import com.example.userserivce.jpaRepository.UserEntity;
import com.example.userserivce.jpaRepository.UserRepository;
import com.example.userserivce.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

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
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        //Bcrypt-> 빈으로 등록해 줘야함(가장 먼저 빈으로 등록되는 곳에 - springbootaplication)
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        //주문정보
        List<ResponseOrder> orders = new ArrayList<>();
        userDto.setOrders(orders);

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
