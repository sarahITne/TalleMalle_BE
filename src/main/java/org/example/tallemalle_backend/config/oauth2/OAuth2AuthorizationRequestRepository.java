package org.example.tallemalle_backend.config.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.util.internal.SerializationUtil;
import org.example.tallemalle_backend.utils.Aes256;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class OAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // 쿠키 확인
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("OAUTH2_REQUEST")) {
                OAuth2AuthorizationRequest oAuth2AuthorizationRequest =
                        (OAuth2AuthorizationRequest) SerializationUtils.deserialize(cookie.getValue().getBytes());

                return oAuth2AuthorizationRequest;
            }
        }

        return null;
    }

    // 쿠키 설정 (첫번째 요청이 들어왔을 때 쿠키 세팅)
    // 사용자 웹 브라우저에 쿠키를 저장해 둘 수 있도록 하는 코드 (소셜 로그인 중간 과정에 필요한 정보를 쿠키로 세팅)
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        // response.addHeader("Set-Cookie", ""); 이거 대신 아래 처럼 쿠키 설정 (세팅 해야될게 문자열로 하기에는 너무 많아서)
        // 쿠키 만들기 (쿠키 세팅)
        Cookie cookie = new Cookie("OAUTH2_REQUEST",
                Aes256.encrypt(SerializationUtils.serialize(authorizationRequest))
        );  // 암호화 해서 세팅 해줘야 함
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(((int) Duration.ofSeconds(300L).toSeconds())); // 원래는 짧게 설정해줘야 함 (10L)

        response.addCookie(cookie);
    }

    // 쿠키 필요 없어지면 삭제
    // 인증이 다 끝나고 나서 쿠키가 필요 없어지면 삭제
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        for(Cookie cookie : request.getCookies()) {
            OAuth2AuthorizationRequest oAuth2AuthorizationRequest =
                    (OAuth2AuthorizationRequest) SerializationUtils.deserialize(
                            Aes256.decrypt(cookie.getValue().getBytes(StandardCharsets.UTF_8))
                    );

            // 아무 값이 엇는 쿠키를 만들어 주고
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            // 0초로 하면 쿠키 만료 시간이 지나서 웹 브라우저가 알아서 쿠키 삭제함 (중요)
            cookie.setMaxAge(((int) Duration.ofSeconds(0L).toSeconds()));
            response.addCookie(cookie);

            return oAuth2AuthorizationRequest;
        }

        return null;
    }
}
