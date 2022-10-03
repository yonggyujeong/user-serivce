package com.example.userserivce.service;

import com.example.userserivce.Dto.UserDto;
import org.springframework.stereotype.Service;


public interface UserService {
    UserDto createUser(UserDto userDto);
}
