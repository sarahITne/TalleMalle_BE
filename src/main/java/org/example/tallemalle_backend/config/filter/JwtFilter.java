package org.example.tallemalle_backend.config.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    // JWT 검사 제외할 URL
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        return path.startsWith("/user/login") ||
                path.startsWith("/user/signup") ||
                path.startsWith("/user/verify");
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
                    String name = jwtUtil.getName(cookie.getValue());
                    String nickname = jwtUtil.getNickname(cookie.getValue());
                    String role = jwtUtil.getRole(cookie.getValue());
                    String status = jwtUtil.getStatus(cookie.getValue());

                    AuthUserDetails user = AuthUserDetails.builder()
                            .idx(idx)
                            .email(email)
                            .name(name)
                            .nickname(nickname)
                            .role(role)
                            .status(status)
                            .build();

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user,
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
