package com.example.userserivce.service;

import com.example.userserivce.Dto.UserDto;
import com.example.userserivce.jpaRepository.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    //DB의 데이터를 가공할 것이라면 Dto로 만들어 가공 후 반환
    //DB의 값을 그냥 바로 반환하기만 한다면 Entity로 그냥 반환해도 됨
    UserDto getUserById(String userId);
    Iterable<UserEntity> getUserByAll();
}
