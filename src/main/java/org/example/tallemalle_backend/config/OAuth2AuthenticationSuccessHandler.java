package org.example.tallemalle_backend.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.utils.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 로그인 성공 후 성공 "응답 처리" 로직 (Jwt 토근 발급)
@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("OAuth 2.0 로그인 성공");

        AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();

        String jwt = jwtUtil.createToken(user);
        response.addHeader("Set-Cookie", "ATOKEN=" + jwt +"; Path=/");

        // 권한 확인 후 리다이렉트 경로 결정
        String role = user.getAuthorities().iterator().next().getAuthority();

        // 로그인 성공 시 프론트엔드로 리다이렉트
        String redirectUrl = "http://localhost:5173";

        if ("ROLE_GUEST".equals(role)) {
            // 추가 정보 입력 페이지로 이동 (토큰을 들고 가야 함)
            redirectUrl += "/signup/extra";
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
