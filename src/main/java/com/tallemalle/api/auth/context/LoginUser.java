package com.tallemalle.api.auth.context;

import io.jsonwebtoken.Claims;

public class LoginUser {
    private final Long userId;
    private final String email;

    public LoginUser(Long userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    // JWT Claims → LoginUser 변환
    public static LoginUser from(Claims claims) {
        return new LoginUser(
                Long.valueOf(claims.get("userId").toString()),
                claims.get("email", String.class)
        );
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
