package com.tallemalle.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tallemalle.api.common.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class JsonParser {
    private static final ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new JavaTimeModule());

    public static <T> T from(HttpServletRequest req, Class<T> clazz) {
        String contentType = req.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            return fromJson(req, clazz);
        } else {
            return fromParams(req, clazz);
        }
    }

    public static <T> T from(HttpResponse<String> res, Class<T> clazz) {
        try {
            return objectMapper.readValue(res.body(), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String from(BaseResponse res) {
        try {
            return objectMapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Json -> DTO
    private static <T> T fromJson(HttpServletRequest req, Class<T> clazz) {
        try {
            return objectMapper.readValue(req.getInputStream(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Query Parameter / Form Data -> DTO
    private static <T> T fromParams(HttpServletRequest req, Class<T> clazz) {
        Map<String, String[]> paramMap = req.getParameterMap();
        Map<String, Object> flatMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values.length == 1) {
                flatMap.put(key, values[0]);
            } else {
                flatMap.put(key, values);
            }
        }
        return objectMapper.convertValue(flatMap, clazz);
    }
}
