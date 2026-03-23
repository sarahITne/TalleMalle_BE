package org.example.tallemalle_backend.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import org.example.tallemalle_backend.profile.data.entity.Profile;

import java.time.LocalDate;
import java.util.Map;

@Schema(name = "UserDto", description = "사용자 관련 DTO 모음")
public class UserDto {

    // 회원가입 요청
    @Schema(name = "User_SignupReq", description = "회원 가입 요청 정보")
    @Getter
    public static class SignupReq {

        @Schema(description = "이메일 계정", example = "user1@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @Schema(description = "비밀번호 (영문, 숫자, 특수문자 조합 8~20자)", example = "password1234!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(message = "비밀번호는 숫자, 영문, 특수문자( !@#$%^&*() )를 조합해 8~20자로 생성해주세요.", regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$")
        private String password;

        @Schema(description = "사용자 실명 (한글)", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(message = "이름은 한글만 가능합니다.", regexp = "^[가-힣]+$")
        private String name;

        @Schema(description = "사용할 닉네임 (한글, 숫자)", example = "탈래말래123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(message = "닉네임은 한글과 숫자만 가능합니다.", regexp = "^[가-힣0-9]+$")
        private String nickname;

        @Schema(description = "전화번호 (하이픈 포함)", example = "010-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(message = "전화번호는 숫자만 입력 가능합니다.", regexp = "^01[0-9]-\\d{3,4}-\\d{4}$")
        private String phoneNumber;

        @Schema(description = "생년월일", example = "1995-01-01")
        private LocalDate birth;

        @Schema(description = "성별 (M: 남성, F: 여성)", example = "M", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String gender;

        public User toEntity(String encodedPassword) {
            User user = User.builder()
                    .email(this.email)
                    .password(encodedPassword)
                    .name(this.name)
                    .build();
            
            Profile profile = Profile.builder()
                    .user(user)
                    .nickname(this.nickname)
                    .phoneNumber(this.phoneNumber)
                    .birth(this.birth)
                    .gender(this.gender)
                    .build();
            
            user.setProfile(profile);
            return user;
        }
    }


    // 회원가입 응답
    @Schema(name = "User_SignupRes", description = "회원 가입 응답 정보")
    @Builder
    @Getter
    public static class SignupRes {
        @Schema(description = "사용자 고유 번호(ID)", example = "1")
        private Long idx;
        @Schema(description = "이메일", example = "user1@test.com")
        private String email;
        @Schema(description = "이름", example = "홍길동")
        private String name;
        @Schema(description = "닉네임", example = "탈래말래123")
        private String nickname;
        @Schema(description = "사용자 상태", example = "IDLE")
        private UserStatus status;

        public static SignupRes from(User entity) {
            return SignupRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .nickname(entity.getProfile() != null ? entity.getProfile().getNickname() : null)
                    .status(entity.getStatus())
                    .build();
        }
    }


    // 본인 인증 확인 요청
    @Schema(name = "User_VerifyIdentityReq", description = "본인 인증 완료 확인 요청 정보")
    public static class VerifyIdentityReq {
        @Schema(
                description = "PortOne 본인 인증 ID",
                example = "identity-verification-266a423b-0d8b-4f4b-943a-e3ec90032bc3",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String identityVerificationId;

        // Getter, Setter (또는 lombok @Getter)
        public String getIdentityVerificationId() { return identityVerificationId; }
    }


    // 소셜 로그인 관련(OAuth) DTO
    @Schema(name = "User_OAuth", description = "소셜 로그인 사용자 정보")
    @Getter
    @Builder
    public static class OAuth {
        @Schema(description = "이메일", example = "4729209808@kakao.social")
        private String email;
        @Schema(description = "이름", example = "이라임")
        private String name;
        @Schema(description = "제공자 (KAKAO, GOOGLE)", example = "KAKAO")
        private String provider;
        @Schema(description = "사용자 권한", example = "ROLE_USER")
        private String role;

        public static OAuth from(Map<String, Object> attributes, String provider) {
            String providerId = null;
            String email = null;
            Map properties = null;
            String name = null;

            if (provider.equals("kakao")) {
                providerId = ((Long) attributes.get("id")).toString();
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
            User user = User.builder()
                    .email(email)
                    .password("social-login")
                    .name(name)
                    .provider(provider.toUpperCase())
                    .role("ROLE_GUEST")   // 권한으로 추가 정보 대상자 구분
                    .build();

            Profile profile = Profile.builder()
                    .user(user)
                    .nickname("임시 닉네임")          // 임시 닉네임
                    .phoneNumber("010-0000-0000")   // 임시 번호
                    .birth(LocalDate.parse("1900-01-01"))    // 임시 생년월일
                    .gender("PENDING")    // 임시 성별
                    .build();

            user.setProfile(profile);
            return user;
        }
    }


    // 소셜 로그인 사용자 회원가입 추가 정보 업데이트 요청 dto
    @Schema(name = "User_ExtraInfoReq", description = "소셜 가입자 추가 정보 입력 요청")
    @Getter
    public static class ExtraInfoReq {
        @Schema(description = "사용할 닉네임", example = "소셜유저123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(message = "닉네임은 한글과 숫자만 가능합니다.", regexp = "^[가-힣0-9]+$")
        private String nickname;

        @Schema(description = "전화번호", example = "010-9876-5432", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(message = "전화번호는 숫자만 입력 가능합니다.", regexp = "^01[0-9]-\\d{3,4}-\\d{4}$")
        private String phoneNumber;

        @Schema(description = "생년월일", example = "1990-05-05")
        private LocalDate birth;

        @Schema(description = "성별 (MALE/FEMALE)", example = "FEMALE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String gender;
    }


    // 소셜 로그인 사용자 회원가입 추가 정보 업데이트 응답
    @Schema(name = "User_ExtraInfoRes", description = "추가 정보 업데이트 응답")
    @Builder
    @Getter
    public static class ExtraInfoRes {
        @Schema(description = "사용자 고유 번호", example = "1")
        private Long idx;
        @Schema(description = "이메일", example = "user1@test.com")
        private String email;
        @Schema(description = "이름", example = "홍길동")
        private String name;
        @Schema(description = "변경된 닉네임", example = "소셜우저123")
        private String nickname;
        @Schema(description = "사용자 상태", example = "IDLE")
        private UserStatus status;

        public static ExtraInfoRes from(User entity) {
            return ExtraInfoRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .nickname(entity.getProfile() != null ? entity.getProfile().getNickname() : null)
                    .status(entity.getStatus())
                    .build();
        }
    }


    // 로그인 요청
    @Schema(name = "User_LoginReq", description = "로그인 요청 정보")
    @Getter
    public static class LoginReq {
        @Schema(description = "이메일 계정", example = "user1@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Email
        private String email;

        @Schema(description = "비밀번호", example = "password1234!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        private String password;
    }


    // 로그인 응답
    @Schema(name = "User_LoginRes", description = "로그인 응답 정보 (JWT는 쿠키로 별도 발급됨)")
    @Builder
    @Getter
    public static class LoginRes {
        @Schema(description = "사용자 고유 번호", example = "1")
        private Long idx;
        @Schema(description = "이메일", example = "user1@test.com")
        private String email;
        @Schema(description = "사용자 권한", example = "ROLE_USER")
        private String role;
        @Schema(description = "사용자 상태", example = "IDLE")
        private UserStatus status;

        public static LoginRes from(AuthUserDetails authUserDetails) {
            return LoginRes.builder()
                    .idx(authUserDetails.getIdx())
                    .email(authUserDetails.getEmail())
                    .role(authUserDetails.getRole())
                    .status(authUserDetails.getStatus())
                    .build();
        }
    }
}
