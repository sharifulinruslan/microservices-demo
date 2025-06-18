package com.example.userservice.service;

import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @CachePut(value = "users", key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        return (User) userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return (User) userRepository.findByEmail(email).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @CacheEvict(value = "users", key = "#id")
    public User deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.delete(user);
        }
        return user;
    }

    public List<User> getUsersByYear(Integer year) {
        return userRepository.findByYear(year);
    }

    public User getUserByYear(Integer year) {
        return userRepository.findByYear(year).stream().findFirst().orElse(null);
    }
}
