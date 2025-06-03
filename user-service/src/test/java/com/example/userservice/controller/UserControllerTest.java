package com.example.userservice.controller;

import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockitoBean
    private UserService userService;

    private User user1;
    private User user2;
    private List<User> users;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setPassword("password1");
        user1.setRole("USER");
        user1.setEmail("user1@mail.com");

        user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setPassword("password2");
        user2.setRole("USER");
        user2.setEmail("user2@mail.com");

        users = List.of(user1, user2);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        doReturn(users).when(userService).getAllUsers();

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[1].name").value(user2.getName()))
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        doReturn(user1).when(userService).getUserById(user1.getId());

        mockMvc.perform(get("/api/users/{id}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user1.getName()));

        verify(userService, times(1)).getUserById(user1.getId());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNothing() throws Exception {
        doReturn(null).when(userService).getUserById(user1.getId());

        mockMvc.perform(get("/api/users/{id}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(userService, times(1)).getUserById(user1.getId());
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() throws Exception {
        doReturn(user1).when(userService).getUserByEmail(user1.getEmail());

        mockMvc.perform(get("/api/users/email/{email}", user1.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user1.getName()));

        verify(userService, times(1)).getUserByEmail(user1.getEmail());
    }

    @Test
    void saveUser_ShouldReturn200_Status() throws Exception {
        doNothing().when(userService).saveUser(user1);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(user1.getName()));

        verify(userService, times(1)).saveUser(user1);
    }

    @Test
    void updateUser_ShouldReturn200_Status() throws Exception {
        doReturn(user1).when(userService).updateUser(user1);

        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user1.getName()));

        verify(userService, times(1)).updateUser(user1);
    }

    @Test
    void deleteUser_ShouldReturn200_Status() throws Exception {
        doNothing().when(userService).deleteUser(user1.getId());

        mockMvc.perform(delete("/api/users/{id}", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(userService, times(1)).deleteUser(user1.getId());
    }
}