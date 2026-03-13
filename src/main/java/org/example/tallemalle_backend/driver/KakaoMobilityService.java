package org.example.tallemalle_backend.driver;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.model.Call;
import org.example.tallemalle_backend.driver.model.DirectionInfo;
import org.example.tallemalle_backend.driver.model.KakaoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class KakaoMobilityService {
    private final String API_URL = "https://apis-navi.kakaomobility.com/v1/directions";
    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public DirectionInfo getDirections(Call call) {
        String origin = call.getStartLng().toPlainString() + "," + call.getStartLat().toPlainString();
        String destination = call.getEndLng().toPlainString() + "," + call.getEndLat().toPlainString();

        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, KakaoResponseDto.class
            );

            if (response.getBody() == null || response.getBody().getRoutes().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "경로 정보를 찾을 수 없습니다.");
            }

            var summary = response.getBody().getRoutes().get(0).getSummary();
            return new DirectionInfo(summary.getDuration(), summary.getDistance());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "경로 계산 중 오류 발생: " + e.getMessage());
        }
    }
}