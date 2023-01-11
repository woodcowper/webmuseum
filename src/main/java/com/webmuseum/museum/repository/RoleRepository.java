package com.webmuseum.museum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webmuseum.museum.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}