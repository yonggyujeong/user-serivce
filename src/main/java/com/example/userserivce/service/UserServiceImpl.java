package com.example.userserivce.service;

import com.example.userserivce.Dto.UserDto;
import com.example.userserivce.jpaRepository.UserEntity;
import com.example.userserivce.jpaRepository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{


    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

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
}
