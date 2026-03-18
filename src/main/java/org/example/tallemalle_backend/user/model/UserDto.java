package org.example.tallemalle_backend.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

public class UserDto {

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

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .email(this.email)
                    .password(encodedPassword)
                    .name(this.name)
                    .nickname(this.nickname)
                    .phoneNumber(this.phoneNumber)
                    .birth(this.birth)
                    .gender(this.gender)
                    .build();
        }
        public User toEntity(String encodedPassword) {
            return User.builder()
                    .email(this.email)
                    .password(encodedPassword)
                    .name(this.name)
                    .nickname(this.nickname)
                    .phoneNumber(this.phoneNumber)
                    .birth(this.birth)
                    .gender(this.gender)
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

        public static SignupRes from(User entity) {
            return SignupRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .nickname(entity.getNickname())
                    .status(entity.getStatus())
                    .build();
        }
    }


    // 소셜 로그인 관련(OAuth) DTO
    @Getter
    @Builder
    public static class OAuth {
        private String email;
        private String name;
        private String provider;
        private String role;

        public static OAuth from(Map<String, Object> attributes, String provider) {
            String providerId = null;
            String email = null;
            Map properties = null;
            String name = null;

            if (provider.equals("kakao")) {
                providerId = ((Long) attributes.get("id")).toString();
                // 받아온 정보 가공
                email = providerId + "@kakao.social";
                properties = (Map) attributes.get("properties");
                name = (String) properties.get("nickname");

            } else if (provider.equals("google")) {
                email = attributes.get("email").toString();
                name = attributes.get("name").toString();
            }

            return OAuth.builder()
                    .email(email)
                    .name(name)
                    .provider(provider)
                    .build();
        }

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .password("social-login")
                    .name(name)
                    .nickname("임시 닉네임")          // 임시 닉네임 (카카오에서 받아올 수 없음, 이후 추가 회원가입 단계에서 정보 입력)
                    .phoneNumber("010-0000-0000")   // 임시 번호
                    .birth(LocalDate.parse("1900-01-01"))    // 임시 생년월일
                    .gender("PENDING")    // 임시 성별
                    .provider(provider.toUpperCase())
                    .role("ROLE_GUEST")   // 권한으로 추가 정보 대상자 구분
                    .build();
        }
    }


    // 소셜 로그인 사용자 회원가입 추가 정보 업데이트
    @Getter
    public static class ExtraInfoReq {
        @NotBlank
        @Pattern(message = "닉네임은 한글과 숫자만 가능합니다.", regexp = "^[가-힣0-9]+$")
        private String nickname;

        @NotBlank
        @Pattern(message = "전화번호는 숫자만 입력 가능합니다.", regexp = "^01[0-9]-\\d{3,4}-\\d{4}$")
        private String phoneNumber;

        private LocalDate birth;

        @NotBlank
        private String gender;
    }


    // 소셜 로그인 사용자 회원가입 추가 정보 업데이트 응답
    @Builder
    @Getter
    public static class ExtraInfoRes {
        private Long idx;
        private String email;
        private String name;
        private String nickname;
        private String status;

        public static ExtraInfoRes from(User entity) {
            return ExtraInfoRes.builder()
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

        public static LoginRes from(AuthUserDetails authUserDetails) {
            return LoginRes.builder()
                    .idx(authUserDetails.getIdx())
                    .email(authUserDetails.getEmail())
                    .name(authUserDetails.getName())
                    .nickname(authUserDetails.getNickname())
                    .role(authUserDetails.getRole())
                    .status(authUserDetails.getStatus())
                    .build();
        }
    }
}