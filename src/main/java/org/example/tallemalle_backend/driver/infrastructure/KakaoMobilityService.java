package org.example.tallemalle_backend.driver.infrastructure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.call.model.Call;
import org.example.tallemalle_backend.driver.infrastructure.model.DirectionInfo;
import org.example.tallemalle_backend.driver.infrastructure.model.KakaoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KakaoMobilityService {
    // Refactor : 최대 10,000 엔트리 / 쓰기 후 15분 만료
    private static final int MAX_CACHE_ENTRIES = 10_000;

    private final String API_URL = "https://apis-navi.kakaomobility.com/v1/directions";
    private final RestTemplate restTemplate;

    // Refactor : 동일 출발·도착에 대한 카카오 응답 캐시 (TTL 후 재조회)
    private final Cache<String, DirectionInfo> directionsCache = Caffeine.newBuilder()
            .maximumSize(MAX_CACHE_ENTRIES)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build();

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    // Refactor : getDirections(Call)은 캐시 조회 → 없을 때만 fetchDirectionsFromApi로 카카오 호출
    public DirectionInfo getDirections(Call call) {
        String origin = call.getStartLng().toPlainString() + "," + call.getStartLat().toPlainString();
        String destination = call.getEndLng().toPlainString() + "," + call.getEndLat().toPlainString();
        String cacheKey = origin + "|" + destination;
        return directionsCache.get(cacheKey, key -> fetchDirectionsFromApi(origin, destination));
    }

    private DirectionInfo fetchDirectionsFromApi(String origin, String destination) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .build()
                .toUriString();

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

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "경로 계산 중 오류 발생: " + e.getMessage());
        }
    }
}
