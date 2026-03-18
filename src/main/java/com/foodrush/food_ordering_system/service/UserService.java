package com.foodrush.food_ordering_system.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodrush.food_ordering_system.entity.User;
import com.foodrush.food_ordering_system.enums.Role;
import com.foodrush.food_ordering_system.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public User registerUser(String email, String password , String firstName, String lastName, String phoneNumber ){
       
        if(existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

}
