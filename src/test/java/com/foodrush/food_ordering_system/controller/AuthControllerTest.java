package com.foodrush.food_ordering_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodrush.food_ordering_system.TestBase;
import com.foodrush.food_ordering_system.dto.request.LoginRequest;
import com.foodrush.food_ordering_system.dto.request.RegistrationRequest;
import com.foodrush.food_ordering_system.entity.User;
import com.foodrush.food_ordering_system.enums.Role;
import com.foodrush.food_ordering_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends TestBase {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("1234567890");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        when(userService.registerUser(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService).registerUser("test@example.com", "password123", "John", "Doe", "1234567890");
    }

    @Test
    void testRegister_InvalidEmail() throws Exception {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("invalid-email");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("1234567890");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testRegister_MissingRequiredFields() throws Exception {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        // Missing all required fields

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testRegister_WeakPassword() throws Exception {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("123"); // Too short
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("1234567890");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testRegister_DuplicateEmail() throws Exception {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("1234567890");

        when(userService.registerUser(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService).registerUser("existing@example.com", "password123", "John", "Doe", "1234567890");
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.CUSTOMER);

        when(userService.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(mockUser));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).findByEmail("test@example.com");
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(userService.findByEmail("test@example.com")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userService).findByEmail("test@example.com");
    }

    @Test
    void testLogin_MissingEmail() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setPassword("password123");
        // Missing email

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findByEmail(anyString());
    }

    @Test
    void testLogin_MissingPassword() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        // Missing password

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findByEmail(anyString());
    }

    @Test
    void testRegister_InvalidPhoneNumber() throws Exception {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("123"); // Too short

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testRegister_InvalidNameFormat() throws Exception {
        // Arrange
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFirstName("John123"); // Contains numbers
        request.setLastName("Doe");
        request.setPhoneNumber("1234567890");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(anyString(), anyString(), anyString(), anyString(), anyString());
    }
}
