package com.tallemalle.api.user.model;

import java.time.LocalDateTime;

public class UserDto {
    private Long userId;
    private String email;
    private String password;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // 회원가입 - 요청 dto
    public static class SignupReq {
        private String email;
        private String password;

        public SignupReq() {
        }

        public SignupReq(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // 회원가입 - 응답 dto
    public static class SignupRes {
        private Long userId;
        private String email;

        public SignupRes() {
        }

        public SignupRes(Long userId, String email) {
            this.userId = userId;
            this.email = email;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // 로그인 - 요청 dto
    public static class LoginReq {
        private String email;
        private String password;

        public LoginReq() {
        }

        public LoginReq(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // 로그인 - 응답 dto
    public static class LoginRes {
        private Long userId;
        private String email;

        public LoginRes() {
        }

        public LoginRes(Long userId, String email) {
            this.userId = userId;
            this.email = email;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

}
