package com.tallemalle.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallemalle.api.common.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class JsonParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T from(HttpServletRequest req, Class<T> clazz) {
        try {
            return objectMapper.readValue(req.getReader(), clazz);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static String from(BaseResponse res) {
        try {
            // 변수이름을 키, 변수에 저장된 값을 값
            // ex) Integer num01 = 10; => { num01: 10 }
            return objectMapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
