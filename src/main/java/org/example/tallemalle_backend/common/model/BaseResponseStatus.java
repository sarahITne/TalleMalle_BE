package org.example.tallemalle_backend.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BaseResponseStatus {
    // 2000번대 성공
    SUCCESS(true, 2000, "요청 성공"),

    // 3000번대 클라이언트 입력 오류, 입력값 검증 오류
    JWT_EXPIRED(false, 3001, "JWT 토큰 만료"),
    JWT_INVALID(false, 3002, "JWT 토큰 유효하지 않음"),
    SIGNUP_DUPLICATE_EMAIL(false, 3003, "중복된 이메일"),
    SIGNUP_INVALID_PASSWORD(false, 3004, "비밀번호는 대소문자, 숫자, 특수문자 포함"),
    SIGNUP_INVALID_UUID(false, 3005, "유효하지 않은 인증값"),


    // 4000번대 실패
    REQUEST_ERROR(false, 4001, "입력값이 잘못되었습니다."),
    NOT_FOUND_DATA(false, 4002, "데이터를 찾을 수 없습니다."),
    ALREADY_JOINED(false, 4003, "이미 참여 중인 모집글이 있습니다."),
    RECRUIT_FULL(false, 4004, "모집이 마감되었거나 인원이 초과되었습니다."),

    // 5000번대 실패
    FAIL(false, 5000, "요청 실패");

    private final boolean success;
    private final int code;
    private final String message;
}
