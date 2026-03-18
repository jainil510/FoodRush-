package com.foodrush.food_ordering_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    

    public AuthResponse(String token ,Long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
