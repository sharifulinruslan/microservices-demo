package com.example.userservice.controller;

import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser1;
    private User testUser2;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setName("test1");
        testUser1.setPassword("password1");
        testUser1.setEmail("test1@example.com");

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setName("test2");
        testUser2.setPassword("password2");
        testUser2.setEmail("test2@example.com");

        userList = Arrays.asList(
                testUser1,
                testUser2
        );
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        doReturn(userList).when(userService).getAllUsers();

        // Act
        List<User> result = userController.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        doReturn(testUser1).when(userService).getUserById(testUser1.getId());

        // Act
        User result = userController.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser1, result);
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        // Arrange
        String email = "test1@email.com";
        doReturn(testUser1).when(userService).getUserByEmail(email);

        // Act
        User result = userController.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(testUser1, result);
        verify(userService, times(1)).getUserByEmail(email);
    }
}