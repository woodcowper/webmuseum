package com.webmuseum.museum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webmuseum.museum.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}