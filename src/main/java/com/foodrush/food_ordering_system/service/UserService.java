package com.foodrush.food_ordering_system.service;

import com.foodrush.food_ordering_system.entity.User;
import com.foodrush.food_ordering_system.enums.Role;
import com.foodrush.food_ordering_system.exception.ResourceNotFoundException;
import com.foodrush.food_ordering_system.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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

    // Admin methods for user management
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public User updateUserRole(Long userId, Role newRole) {
        log.info("Updating user {} role to: {}", userId, newRole);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setRole(newRole);
        return userRepository.save(user);
    }

    public User toggleUserStatus(Long userId) {
        log.info("Toggling status for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    public Long getTotalUsersCount() {
        log.info("Getting total users count");
        return userRepository.count();
    }

}
