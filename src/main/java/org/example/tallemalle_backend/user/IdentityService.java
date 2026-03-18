package org.example.tallemalle_backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class IdentityService {
    @Value("${portone.api.secret}")
    private String portoneSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getAccessToken() {
        String url = "https://api.portone.io/login/api-secret";

        Map<String, String> body = Map.of("apiSecret", portoneSecret);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        return (String) response.getBody().get("accessToken");
    }

    public Map<String, Object> confirmIdentity(String identityVerificationId) {
        try {
            // 1. Access Token 발급 요청
            String accessToken = getAccessToken();

            // 2. 받은 토큰으로 본인인증 내역 조회
            String url = "https://api.portone.io/identity-verifications/" + identityVerificationId;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken); // 발급받은 토큰 설정

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                String status = (String) body.get("status");    // 포트원 응답 구조에서 상태값 확인

                if ("VERIFIED".equals(status)) {
                    // 'verifiedCustomer' 객체 안에 이름, 번호, 생년월일 등이 들어있음
                    return (Map<String, Object>) body.get("verifiedCustomer");
                }
            }
        } catch (Exception e) {
            System.err.println("본인인증 확인 중 오류 발생: " + e.getMessage());
        }
        return null;
    }
}
