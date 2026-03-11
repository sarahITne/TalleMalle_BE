package org.example.tallemalle_backend.user.model;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

public class UserDto {
    @Getter
    @Builder
    public static class OAuth {
        private String email;
        private String name;
        private String provider;
        private boolean enable;
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
            } else if(provider.equals("google")){
                email = (String)attributes.get("email");
                name = (String) attributes.get("name");
            }

            return OAuth.builder()
                    .email(email)
                    .name(name)
                    .provider(provider)
                    .enable(true)
                    .role("ROLE_USER")
                    .build();
        }

        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .password(this.provider)
                    .enable(this.enable)
                    .role(this.role)
                    .build();
        }
    }

    @Getter
    public static class SignupReq {

        @Pattern(message = "이메일 형식이 아닙니다.", regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$")
        private String email;

        private String name;

        private String password;

        @Pattern(message = "role은 USER 또는 DRIVER만 가능합니다.", regexp = "USER|DRIVER")
        private String role;

        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .name(this.name)
                    .password(this.password)
                    .role(this.role)
                    .enable(false)
                    .build();
        }
    }


    @Builder
    @Getter
    public static class SignupRes {
        private Long idx;
        private String email;
        private String name;
        private String role;

        public static SignupRes from(User entity) {
            return SignupRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .role(entity.getRole())
                    .build();
        }
    }

    @Getter
    public static class LoginReq {
        private String email;
        private String password;
        private String role;
    }

    @Builder
    @Getter
    public static class LoginRes {
        private Long idx;
        private String email;
        private String name;
        private String role;

        public static LoginRes from(User entity) {
            return LoginRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .role(entity.getRole())
                    .build();
        }
    }
}
