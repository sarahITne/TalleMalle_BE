package org.example.tallemalle_backend.config.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
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
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        return path.startsWith("/user/login") ||
                path.startsWith("/user/signup") ||
                path.startsWith("/user/verify");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("ATOKEN")) {
                    try {
                        // JwtUtil에서 토큰 생성 및 확인하도록 리팩토링
                        String email = jwtUtil.getEmail(cookie.getValue());
                        Long idx = jwtUtil.getUserIdx(cookie.getValue());
                        String role = jwtUtil.getRole(cookie.getValue());

                        AuthUserDetails user = AuthUserDetails.builder()
                                .idx(idx)
                                .email(email)
                                .role(role)
                                .build();

                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(new SimpleGrantedAuthority(role))
                            );
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (JwtException | IllegalArgumentException e) {
                        log.warn("유효하지 않거나 만료된 ATOKEN 무시: path={}, reason={}", request.getRequestURI(), e.getMessage());
                        SecurityContextHolder.clearContext();

                        Cookie expiredCookie = new Cookie("ATOKEN", "");
                        expiredCookie.setPath("/");
                        expiredCookie.setMaxAge(0);
                        response.addCookie(expiredCookie);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
