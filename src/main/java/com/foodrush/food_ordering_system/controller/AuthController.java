package com.foodrush.food_ordering_system.controller;

import com.foodrush.food_ordering_system.dto.request.LoginRequest;
import com.foodrush.food_ordering_system.dto.request.RegistrationRequest;
import com.foodrush.food_ordering_system.dto.response.AuthResponse;
import com.foodrush.food_ordering_system.dto.response.UserResponse;
import com.foodrush.food_ordering_system.entity.User;
import com.foodrush.food_ordering_system.security.JwtTokenProvider;
import com.foodrush.food_ordering_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

   @PostMapping("/register")
   public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {
       try {
           System.out.println("Registration request received: " + request.getEmail());
           
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
           System.out.println("Registration error: " + e.getMessage());
           e.printStackTrace();
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
       }
   }
   
   @PostMapping("/login")
   public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
       try {
           Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(
                   request.getEmail(), 
                   request.getPassword()
               )
           );
           
           String token = jwtTokenProvider.generateToken(authentication);
           AuthResponse response = new AuthResponse(token, 86400000L);
           
           return ResponseEntity.ok(response);
           
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       }
   }
}



