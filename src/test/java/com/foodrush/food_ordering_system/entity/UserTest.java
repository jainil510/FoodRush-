package com.foodrush.food_ordering_system.entity;

import com.foodrush.food_ordering_system.TestBase;
import com.foodrush.food_ordering_system.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest extends TestBase {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("1234567890");
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals(Role.CUSTOMER, user.getRole());
        assertTrue(user.isEnabled());
    }

    @Test
    void testUserDetailsImplementation() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setEnabled(true);
        user.setRole(Role.ADMIN);

        // Test UserDetails interface methods
        assertEquals("test@example.com", user.getUsername());
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());

        // Test authorities
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testDefaultRole() {
        User user = new User();
        // Default role should be CUSTOMER
        assertEquals(Role.CUSTOMER, user.getRole());
    }

    @Test
    void testUserWithDifferentRoles() {
        User customer = new User();
        customer.setRole(Role.CUSTOMER);
        assertEquals("ROLE_CUSTOMER", customer.getAuthorities().iterator().next().getAuthority());

        User restaurantOwner = new User();
        restaurantOwner.setRole(Role.RESTAURANT_OWNER);
        assertEquals("ROLE_RESTAURANT_OWNER", restaurantOwner.getAuthorities().iterator().next().getAuthority());

        User admin = new User();
        admin.setRole(Role.ADMIN);
        assertEquals("ROLE_ADMIN", admin.getAuthorities().iterator().next().getAuthority());
    }
}
