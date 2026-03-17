package org.example.tallemalle_backend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.UserDto;
import org.example.tallemalle_backend.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody UserDto.SignupReq dto) {
        UserDto.SignupRes result = userService.signup(dto);
        return ResponseEntity.ok(result);
    }


    // 이메일 중복 확인
    @GetMapping("/signup/check-email")
    public ResponseEntity emailCheck(String email) {
        // true - 사용 가능한 이메일, false - 중복된 이메일
        boolean available = userService.emailCheck(email);

        String message = available
                ? "사용 가능한 이메일입니다."
                : "이미 사용 중인 이메일입니다.";

        return ResponseEntity.ok(message);
    }

    // 닉네임 중복 확인
    @GetMapping("/signup/check-nickname")
    public ResponseEntity nicknameCheck(String nickname) {
        // true - 사용 가능한 닉네임, false - 중복된 닉네임
        boolean available = userService.nicknameCheck(nickname);

        String message = available
                ? "사용 가능한 닉네임입니다."
                : "이미 사용 중인 닉네임입니다.";

        return ResponseEntity.ok(message);
    }


    // 이메일 인증
    @GetMapping("/verify")
    public ResponseEntity verify(String uuid) {
        userService.verify(uuid);

        // 인증 성공하면 프론트로 리다이렉트
        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create("http://localhost:5173/login"))
                .build();
    }


    // 소셜 로그인 한 사용자 추가 정보 업데이트
    @PatchMapping("/signup/extra")
    public ResponseEntity extraInfo(
            @AuthenticationPrincipal AuthUserDetails user,
            @Valid @RequestBody UserDto.ExtraInfoReq dto
    ) {
        UserDto.ExtraInfoRes result = userService.extraInfo(user.getEmail(), dto);

        // 추가 정보 입력 후 로그인 페이지로 리다이렉트
        return ResponseEntity.ok(result);
    }


    // 로그인
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDto.LoginReq dto) {
        String prefixedEmail = "USER_" + dto.getEmail();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(prefixedEmail, dto.getPassword(), null);

        Authentication authentication = authenticationManager.authenticate(token);  // 여기서 UserService의 loadUserByUsername 메소드로 이동
        AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();     // 현재 로그인한 사용자의 객체를 꺼내는 메소드

        if(user != null) {
            String jwt = jwtUtil.createToken(user);
            return ResponseEntity.ok()
                    .header("Set-Cookie", "ATOKEN=" + jwt + "; path=/")   // JWT 토큰을 헤더로 설정해서 응답 (쿠키)
                    .body(UserDto.LoginRes.from(user));  // 응답 결과로 로그인한 사용자 정보 같이 반환
        }

        return ResponseEntity.ok("로그인 실패");
    }

}
