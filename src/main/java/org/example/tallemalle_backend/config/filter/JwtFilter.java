package org.example.tallemalle_backend.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.auth.model.AuthDriverDetails; // 🌟 기사 객체 import (경로 확인해주세요)
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.UserStatus;
import org.example.tallemalle_backend.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    // JWT 검사 제외할 URL
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // OPTIONS 메서드는 무조건 필터 제외 (CORS 해결)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        return path.startsWith("/user/login") ||
                path.equals("/user/signup") ||
                path.startsWith("/user/verify") ||
                path.startsWith("/driver/login") ||
                path.startsWith("/driver/signup");
    }

    // 핵심 로직, 실제 인증 처리 로직, SecurityContext에 유저 정보 저장
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                // ATOKEN 쿠키 찾기
                if (cookie.getName().equals("ATOKEN")) {

                    // JWT에서 정보 꺼내기
                    Long idx = jwtUtil.getUserIdx(cookie.getValue());
                    String email = jwtUtil.getEmail(cookie.getValue());
                    String role = jwtUtil.getRole(cookie.getValue());

                    // 권한(role)에 따라 Principal 객체를 다르게 생성
                    Object principal;
                    if ("DRIVER".equals(role)) {
                        principal = AuthDriverDetails.builder()
                                .idx(idx)
                                .email(email)
                                .role(role)
                                .build();
                    } else {
                        principal = AuthUserDetails.builder()
                                .idx(idx)
                                .email(email)
                                .role(role)
                                .build();
                    }

                    // principal 객체를 넘겨줄 때 role에 맞는 GrantedAuthority를 부여
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority(role))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // 다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }
}