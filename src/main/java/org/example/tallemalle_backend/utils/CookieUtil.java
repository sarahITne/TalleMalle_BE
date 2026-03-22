package org.example.tallemalle_backend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

@Component
@RequiredArgsConstructor
public class CookieUtil {
    @Value("${app.cookie.domain}")
    private String cookieDomain;

    @Value("${app.cookie.max-age}")
    private long cookieMaxAge;

    @Value("${app.cookie.secure}")
    private boolean isSecure;

    @Value("${app.cookie.same-site}")
    private String sameSite;

    // 로그인 시 쿠키 생성
    public ResponseCookie createCookie(String token) {
        // ATOKEN 쿠키 생성 : 개발 환경과 배포 환경에 따라 쿠키 설정을 다르게 해야하므로 변수 사용 (yml 파일 참고)
        return ResponseCookie.from("ATOKEN", token)
                .path("/")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(sameSite)
                .domain(cookieDomain)
                .maxAge(cookieMaxAge)
                .build();
    }

    // 로그아웃 시 쿠키 삭제 (Max-Age를 0으로 설정)
    public ResponseCookie removeCookie() {
        return ResponseCookie.from("ATOKEN", "")
                .path("/")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(sameSite)
                .domain(cookieDomain)
                .maxAge(0) // 핵심: maxAge를 0으로 설정 : 즉시 삭제
                .build();
    }
}
