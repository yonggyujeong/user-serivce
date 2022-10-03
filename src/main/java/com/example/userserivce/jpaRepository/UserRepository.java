package com.example.userserivce.jpaRepository;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> { //엔티티, 기본키의 클래스

}
