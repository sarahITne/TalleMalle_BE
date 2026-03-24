package org.example.tallemalle_backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.tallemalle_backend.driver.auth.model.AuthDriverDetails;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String key;

    @Value("${jwt.expire}")
    private int expire;

    public SecretKey getEncodedKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    // 토큰 생성 (User)
    public String createToken(AuthUserDetails user) {
        return Jwts.builder()
                .claim("idx", user.getIdx())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(getEncodedKey())
                .compact();
    }

    // 토큰 생성 (Driver)
    public String createToken(AuthDriverDetails user) {
        return Jwts.builder()
                .claim("idx", user.getIdx())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(getEncodedKey())
                .compact();
    }

    // 1. 토큰에서 전체 Claims 추출 (공통 메서드)
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getEncodedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 검증 후 위 메서드를 이용하여 필요한 값 가져오기
    public Long getUserIdx(String token) {
        return extractAllClaims(token).get("idx", Long.class);
    }

    public String getEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    public String getRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
}
