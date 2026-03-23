package org.example.tallemalle_backend.driver.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.driver.auth.model.DriverDto;
import org.example.tallemalle_backend.driver.auth.model.AuthDriverDetails;
import org.example.tallemalle_backend.utils.CookieUtil;
import org.example.tallemalle_backend.utils.JwtUtil;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Driver Auth API", description = "드라이버 회원가입·로그인 API")
@CrossOrigin
@RequestMapping("/driver")
@RestController
@RequiredArgsConstructor
public class DriverUserController {
    private final DriverUserService driverUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Operation(summary = "드라이버 회원가입", description = "이메일·비밀번호 기반 드라이버 계정을 등록하는 기능")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody DriverDto.SignupReq dto) {
        DriverDto.SignupRes result =  driverUserService.signup(dto);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "드라이버 이메일 중복 확인", description = "드라이버 회원가입 시 이메일 중복 여부를 확인하는 기능")
    @GetMapping({"/signup/check-email", "/check-email"})
    public ResponseEntity<?> emailCheck(@RequestParam String email) {
        boolean available = driverUserService.emailCheck(email);
        return ResponseEntity.ok(available);
    }

    @Operation(summary = "드라이버 닉네임 중복 확인", description = "드라이버 회원가입 시 닉네임 중복 여부를 확인하는 기능")
    @GetMapping("/signup/check-nickname")
    public ResponseEntity<?> nicknameCheck(@RequestParam String nickname) {
        boolean available = driverUserService.nicknameCheck(nickname);
        return ResponseEntity.ok(available);
    }

    @Operation(summary = "드라이버 본인 인증", description = "PortOne 본인인증 결과를 조회해 검증 정보를 반환하는 기능")
    @PostMapping("/verify-identity")
    public ResponseEntity<?> verifyIdentity(@RequestBody Map<String, String> request) {
        String identityVerificationId = request.get("identityVerificationId");
        Map<String, Object> verified = driverUserService.confirmIdentity(identityVerificationId);
        if (verified == null || verified.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "본인인증 실패"));
        }
        return ResponseEntity.ok(Map.of("message", "본인인증 성공", "userInfo", verified));
    }

    @Operation(summary = "드라이버 로그인", description = "드라이버 계정으로 로그인하고 JWT 쿠키(ATOKEN)를 발급하는 기능")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody DriverDto.LoginReq dto) {
        try {
            String prefixedEmail = "DRIVER_" + dto.getEmail();
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(prefixedEmail, dto.getPassword(), null);

            Authentication authentication = authenticationManager.authenticate(token);
            AuthDriverDetails driver = (AuthDriverDetails) authentication.getPrincipal();

            if (!"DRIVER".equals(driver.getRole())) {
                throw BaseException.from(BaseResponseStatus.DRIVER_ROLE_REQUIRED);
            }

            String jwt = jwtUtil.createToken(driver);
            ResponseCookie cookie = cookieUtil.createCookie(jwt);

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body(DriverDto.LoginRes.from(driver));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.fail(BaseResponseStatus.USER_NOT_FOUND));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.fail(BaseResponseStatus.PASSWORD_WRONG));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.fail(BaseResponseStatus.LOGIN_FAILED));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.fail(BaseResponseStatus.LOGIN_FAILED));
        }
    }

    @Operation(summary = "로그인한 드라이버 식별", description = "현재 ATOKEN 기준으로 로그인된 드라이버 정보를 반환하는 기능")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentDriver(@AuthenticationPrincipal AuthDriverDetails driver) {
        if (driver != null) {
            return ResponseEntity.ok(DriverDto.LoginRes.from(driver));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
    }

    @Operation(summary = "드라이버 로그아웃", description = "ATOKEN 쿠키를 제거하여 로그아웃 처리하는 기능")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = cookieUtil.removeCookie();
        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body("로그아웃 성공");
    }

}
