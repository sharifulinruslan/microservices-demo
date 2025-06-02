package com.example.userservice.service;

import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;
    @InjectMocks
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testUser");
        testUser.setPassword("password");
        testUser.setEmail("test@email.com");
        testUser.setRole("testRole");
    }

    @Test
    void saveUser_ShouldCallRepositorySave() {
        userService.saveUser(testUser);

        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUser_ShouldUpdateAndCacheUser() {
        doReturn(testUser).when(userRepository).save(testUser);

        User result = userService.updateUser(testUser);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testUser);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        doReturn(Optional.of(testUser)).when(userRepository).findById(1L);
        lenient().doReturn(cache).when(cacheManager).getCache("users");

        User result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testUser);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNull() {
        doReturn(Optional.empty()).when(userRepository).findById(1L);

        User result = userService.getUserById(1L);

        assertThat(result).isNull();
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("test@email.com");

        User result = userService.getUserByEmail("test@email.com");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testUser);
        verify(userRepository, times(1)).findByEmail("test@email.com");
    }

    @Test
    void getUserByEmail_WhenUserDoesNotExist_ShouldReturnNull() {
        String dummyEmail = "dummy@email.com";

        doReturn(Optional.empty()).when(userRepository).findByEmail(dummyEmail);

        User result = userService.getUserByEmail(dummyEmail);

        assertThat(result).isNull();
        verify(userRepository, times(1)).findByEmail(dummyEmail);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setId(2L);
        user1.setName("testUser2");
        user1.setEmail("test2@email.com");
        List<User> expectedUsers = Arrays.asList(testUser, user1);
        doReturn(expectedUsers).when(userRepository).findAll();

        List<User> result = userService.getAllUsers();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(expectedUsers);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteAndEvictCacheUser() {
        doReturn(Optional.of(testUser)).when(userRepository).findById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldDoNothing() {
        doReturn(Optional.empty()).when(userRepository).findById(1L);

        userService.deleteUser(1L);

        verify(userRepository, never()).delete(any());
        verify(cacheManager, never()).getCache(anyString());
    }
}