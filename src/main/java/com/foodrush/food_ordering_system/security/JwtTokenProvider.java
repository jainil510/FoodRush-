package com.foodrush.food_ordering_system.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    
        @Value("${jwt.secret}")  
        private String jwtSecret;

        @Value("${jwt.expiration}")
        private int jwtExpirationMs;

       
       
        private SecretKey getSigningKey() {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
}
        

        public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
      
        public String getUsernameFromToken(String token){
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        }    
        
        public boolean validateToken(String token) {
            try {
                Jwts.parserBuilder()
                         .setSigningKey(getSigningKey())
                         .build()
                         .parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

}

