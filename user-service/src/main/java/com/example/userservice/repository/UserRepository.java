package com.example.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository<Long, User> extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
