package org.example.tallemalle_backend.driver.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class DriverDto {

    // 회원가입 요청
    @Getter
    public static class SignupReq {

        @NotBlank
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank
        @Pattern(message = "비밀번호는 숫자, 영문, 특수문자( !@#$%^&*() )를 조합해 8~20자로 생성해주세요.", regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$")
        private String password;

        @NotBlank
        @Pattern(message = "이름은 한글만 가능합니다.", regexp = "^[가-힣]+$")
        private String name;

        @NotBlank
        @Pattern(message = "닉네임은 한글과 숫자만 가능합니다.", regexp = "^[가-힣0-9]+$")
        private String nickname;

        @NotBlank
        @Pattern(message = "전화번호는 숫자만 입력 가능합니다.", regexp = "^01[0-9]-\\d{3,4}-\\d{4}$")
        private String phoneNumber;

        private LocalDate birth;

        @NotBlank
        private String gender;

        @NotBlank
        private String identityVerificationId;

        public Driver toEntity(String encodedPassword) {
            return Driver.builder()
                    .email(this.email)
                    .password(encodedPassword)
                    .name(this.name)
                    .nickname(this.nickname)
                    .phoneNumber(this.phoneNumber)
                    .birth(this.birth)
                    .gender(this.gender)
                    .enable(true)
                    .build();
        }
    }


    // 회원가입 응답
    @Builder
    @Getter
    public static class SignupRes {
        private Long idx;
        private String email;
        private String name;
        private String nickname;
        private String status;

        public static SignupRes from(Driver entity) {
            return SignupRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .nickname(entity.getNickname())
                    .status(entity.getStatus())
                    .build();
        }
    }


    // 로그인 요청
    @Getter
    public static class LoginReq {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;
    }


    // 로그인 응답
    @Builder
    @Getter
    public static class LoginRes {
        private Long idx;
        private String email;
        private String name;
        private String nickname;
        private String role;
        private String status;

        public static LoginRes from(AuthDriverDetails authDriverDetails) {
            return LoginRes.builder()
                    .idx(authDriverDetails.getIdx())
                    .email(authDriverDetails.getEmail())
                    .name(authDriverDetails.getName())
                    .nickname(authDriverDetails.getNickname())
                    .role(authDriverDetails.getRole())
                    .status(authDriverDetails.getStatus())
                    .build();
        }
    }
}