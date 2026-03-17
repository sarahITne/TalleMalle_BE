package org.example.tallemalle_backend.driver.infrastructure.model;

import lombok.Data;

import java.util.List;

@Data
public class KakaoResponseDto {
    private List<Route> routes;

    @Data
    public static class Route {
        private Summary summary;
    }

    @Data
    public static class Summary {
        private int duration;
        private int distance;
    }
}
