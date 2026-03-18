package com.foodrush.food_ordering_system.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodrush.food_ordering_system.dto.request.LoginRequest;
import com.foodrush.food_ordering_system.dto.request.RegistrationRequest;
import com.foodrush.food_ordering_system.dto.response.UserResponse;
import com.foodrush.food_ordering_system.entity.User;
import com.foodrush.food_ordering_system.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final UserService userService;

   @PostMapping("/register")
   public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
       try {
           User user = userService.registerUser(
                   request.getEmail(),
                   request.getPassword(),
                   request.getFirstName(),
                   request.getLastName(),
                   request.getPhoneNumber()
           );
           
           UserResponse response = new UserResponse(
                   user.getId(),
                   user.getEmail(),
                   user.getFirstName(),
                   user.getLastName(),
                   user.getPhoneNumber(),
                   user.getRole(),
                   user.isEnabled(),
                   user.getCreatedAt().toString()
           );
           
           return ResponseEntity.status(HttpStatus.CREATED).body(response);
           
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
       }
   }
   
   @PostMapping("/login")
   public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {
       return ResponseEntity.ok("Login endpoint - JWT implementation coming next");
   }
        
}



