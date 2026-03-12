package org.example.tallemalle_backend.user.model;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class UserDto {

    @Getter
    public static class SignupReq {

        @Pattern(message = "이메일 형식이 아닙니다.", regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$")
        private String email;

        @Pattern(message = "비밀번호는 숫자, 영문, 특수문자( !@#$%^&*() )를 조합해 8~20자로 생성해주세요.", regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$")
        private String password;

        @Pattern(message = "이름은 한글만 가능합니다.", regexp = "^[가-힣]+$")
        private String name;

        @Pattern(message = "닉네임은 한글과 숫자만 가능합니다.", regexp = "^[가-힣0-9]+$")
        private String nickname;

        @Pattern(message = "전화번호는 숫자만 입력 가능합니다.", regexp = "^01[0-9]-\\d{3,4}-\\d{4}$")
        private String phoneNumber;

        private LocalDate birth;

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
//                    .enable(false)
                    .build();
        }
    }


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

    @Getter
    public static class LoginReq {
        private String email;
        private String password;
    }

    @Builder
    @Getter
    public static class LoginRes {
        private Long idx;
        private String email;
        private String name;

        public static LoginRes from(User entity) {
            return LoginRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .build();
        }
    }
}
