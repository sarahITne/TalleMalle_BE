package org.example.tallemalle_backend.config.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.utils.CookieUtil;
import org.example.tallemalle_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

// 로그인 성공 후 성공 "응답 처리" 로직 (Jwt 토근 발급)
@RequiredArgsConstructor
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Value("${app.front-url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("OAuth 2.0 로그인 성공");

        AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.createToken(user);

        // 쿠키 세팅 : CookieUtil 클래스에 구현해놓은 메소드 이용
        ResponseCookie cookie = cookieUtil.createCookie(jwt);

        response.addHeader("Set-Cookie", cookie.toString());

        // 권한 확인 후 리다이렉트 경로 결정
        String role = user.getAuthorities().iterator().next().getAuthority();

        // 로그인 성공 시 프론트엔드로 리다이렉트
        String redirectUrl = UriComponentsBuilder.fromUriString(frontUrl + "/social/success")
                .queryParam("idx", user.getIdx())
                .queryParam("email", user.getEmail())
                .queryParam("name", user.getName())
                .queryParam("nickname", user.getNickname())
                .queryParam("role", user.getRole())
                .queryParam("status", user.getStatus())
                .build().encode().toUriString();

        if ("ROLE_GUEST".equals(role)) {
            // 추가 정보 입력 페이지로 이동 (토큰을 들고 가야 함)
            redirectUrl += "/signup/extra";
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
