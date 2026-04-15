package com.foodrush.food_ordering_system.security;

import com.foodrush.food_ordering_system.TestBase;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest extends TestBase {

    private JwtTokenProvider jwtTokenProvider;
    private Key key;
    private String validToken;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256).build();
        
        // Set test values
        jwtTokenProvider.setSecretKey("testSecretKeyForTestingOnly123456789");
        jwtTokenProvider.setJwtExpirationInMs(3600000); // 1 hour
        
        userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .roles("USER")
                .build();
        
        validToken = jwtTokenProvider.generateToken(userDetails);
    }

    @Test
    void testGenerateToken_Success() {
        // Act
        String token = jwtTokenProvider.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.startsWith("eyJ")); // JWT tokens start with eyJ
    }

    @Test
    void testGetUsernameFromToken_Success() {
        // Act
        String username = jwtTokenProvider.getUsernameFromToken(validToken);

        // Assert
        assertEquals("test@example.com", username);
    }

    @Test
    void testGetUsernameFromToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () ->
                jwtTokenProvider.getUsernameFromToken(invalidToken)
        );
    }

    @Test
    void testGetExpirationDateFromToken_Success() {
        // Act
        Date expiration = jwtTokenProvider.getExpirationDateFromToken(validToken);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testValidateToken_ValidToken() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken(validToken);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Arrange
        String expiredToken = createExpiredToken();

        // Act
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testGetAuthenticationFromToken_Success() {
        // Act
        Authentication authentication = jwtTokenProvider.getAuthentication(validToken);

        // Assert
        assertNotNull(authentication);
        assertEquals("test@example.com", authentication.getName());
        assertTrue(authentication.isAuthenticated());
    }

    @Test
    void testGetAuthenticationFromToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () ->
                jwtTokenProvider.getAuthentication(invalidToken)
        );
    }

    @Test
    void testTokenClaims() {
        // Act
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(validToken)
                .getBody();

        // Assert
        assertNotNull(claims);
        assertEquals("test@example.com", claims.getSubject());
        assertTrue(claims.get("authorities") != null);
    }

    @Test
    void testTokenWithDifferentSecret() {
        // Arrange
        JwtTokenProvider differentProvider = new JwtTokenProvider();
        differentProvider.setSecretKey("differentSecretKey");
        differentProvider.setJwtExpirationInMs(3600000);

        String token = differentProvider.generateToken(userDetails);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testTokenExpiration() {
        // Arrange
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider();
        shortLivedProvider.setSecretKey("testSecretKeyForTestingOnly123456789");
        shortLivedProvider.setJwtExpirationInMs(1); // 1 millisecond

        String shortLivedToken = shortLivedProvider.generateToken(userDetails);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isValid = shortLivedProvider.validateToken(shortLivedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testGenerateTokenForDifferentUsers() {
        // Arrange
        UserDetails user1 = User.builder()
                .username("user1@example.com")
                .password("password")
                .roles("USER")
                .build();

        UserDetails user2 = User.builder()
                .username("user2@example.com")
                .password("password")
                .roles("ADMIN")
                .build();

        // Act
        String token1 = jwtTokenProvider.generateToken(user1);
        String token2 = jwtTokenProvider.generateToken(user2);

        // Assert
        assertNotEquals(token1, token2);
        assertEquals("user1@example.com", jwtTokenProvider.getUsernameFromToken(token1));
        assertEquals("user2@example.com", jwtTokenProvider.getUsernameFromToken(token2));
    }

    @Test
    void testTokenWithSpecialCharacters() {
        // Arrange
        UserDetails specialUser = User.builder()
                .username("user+special@example.com")
                .password("password")
                .roles("USER")
                .build();

        // Act
        String token = jwtTokenProvider.generateToken(specialUser);

        // Assert
        assertNotNull(token);
        assertEquals("user+special@example.com", jwtTokenProvider.getUsernameFromToken(token));
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    private String createExpiredToken() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 1000); // Expired 1 second ago

        return Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
