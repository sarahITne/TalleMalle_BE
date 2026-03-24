package org.example.tallemalle_backend.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.model.BaseResponse;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.UserDto;
import org.example.tallemalle_backend.utils.CookieUtil;
import org.example.tallemalle_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Tag(name = "User API", description = "회원가입·로그인·인증 관련 API")
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Value("${app.front-url}")
    private String frontUrl;

    // 회원 가입
    @Operation(summary = "회원가입", description = "사용자에게 필요한 정보를 받아 회원가입 하는 기능")
    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody UserDto.SignupReq dto) {
        UserDto.SignupRes result = userService.signup(dto);
        return ResponseEntity.ok(result);
    }


    // 본인 인증
    @Operation(summary = "본인 인증 확인", description = "프론트에서 받은 identityVerificationId를 통해 PortOne 본인 인증 성공 여부를 확인합니다.")
    @PostMapping("/verify-identity")
    public ResponseEntity verifyIdentity(@RequestBody UserDto.VerifyIdentityReq request) {
        String identityVerificationId = request.getIdentityVerificationId();
        // 서비스 호출 (본인인증 성공 여부 반환)
        Map<String, Object> userInfo = userService.confirmIdentity(identityVerificationId);

        if (userInfo != null) {
            return ResponseEntity.ok(Map.of(
                    "message", "본인인증 성공",
                    "userInfo", userInfo // 인증된 사용자 정보 포함
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "본인인증 실패"));
        }
    }


    // 이메일 중복 확인
    @Operation(summary = "이메일 중복 확인", description = "입력한 이메일의 사용 가능 여부를 확인합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "이메일 중복 확인 조회 성공 (true: 사용 가능, false: 중복/사용 불가)"
    )
    @GetMapping("/signup/check-email")
    public ResponseEntity emailCheck(String email) {
        // true - 사용 가능한 이메일, false - 중복된 이메일
        boolean available = userService.emailCheck(email);

        return ResponseEntity.ok(available);
    }

    // 닉네임 중복 확인
    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임의 사용 가능 여부를 확인합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "닉네임 중복 확인 조회 성공 (true: 사용 가능, false: 중복/사용 불가)"
    )
    @GetMapping("/signup/check-nickname")
    public ResponseEntity nicknameCheck(String nickname) {
        // true - 사용 가능한 닉네임, false - 중복된 닉네임
        boolean available = userService.nicknameCheck(nickname);

        return ResponseEntity.ok(available);
    }

    // 인증 메일 재전송
    @Operation(summary = "이메일 인증 메일 재전송", description = "가입한 이메일로 인증 메일을 다시 보냅니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재전송 성공"),
            @ApiResponse(responseCode = "400", description = "가입되지 않은 이메일 주소")
    })
    @PostMapping("/resend-verify")
    public ResponseEntity<?> resendVerify(
            @Parameter(description = "재전송할 이메일 주소", example = "user@example.com")
            @RequestParam String email
    ) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "이메일 주소가 필요합니다."));
        }

        userService.resendVerificationMail(email);
        return ResponseEntity.ok(Map.of("message", "인증 메일이 재전송되었습니다."));
    }


    // 이메일 인증 (완료)
    @Operation(summary = "이메일 인증 처리", description = "인증 링크 클릭 시 호출되며 회원가입 시 입력한 이메일 인증을 완료 처리 후 사용자의 계정을 활성화 합니다.")
    @GetMapping("/verify")
    public ResponseEntity verify(String uuid) {
        userService.verify(uuid);

        // 인증 성공하면 프론트로 리다이렉트
        return ResponseEntity
                .status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(frontUrl + "/email/verify-success"))
                .build();
    }


    // 소셜 로그인 한 사용자 추가 정보 업데이트
    @Operation(summary = "소셜 로그인으로 회원가입 시 추가 정보 입력", description = "소셜 로그인을 통해 회원가입 후 부족한 정보(전화번호 등)를 입력받아 계정을 활성화합니다.")
    @PatchMapping("/signup/extra")
    public ResponseEntity extraInfo(
            @AuthenticationPrincipal AuthUserDetails user,
            @Valid @RequestBody UserDto.ExtraInfoReq dto
    ) {
        UserDto.ExtraInfoRes result = userService.extraInfo(user.getEmail(), dto);

        // 추가 정보 입력을 위한 임시 토큰 삭제
        ResponseCookie cookie = cookieUtil.removeCookie();

        // 추가 정보 입력 후 로그인 페이지로 리다이렉트
        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(result);
    }


    // 로그인
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 시도하고 성공 시 JWT 토큰과 쿠키를 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserDto.LoginReq dto) {
        try {
            String prefixedEmail = "USER_" + dto.getEmail();

            // 1. 인증 토큰 생성
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(prefixedEmail, dto.getPassword(), null);

            // 2. 인증 시도 (비밀번호가 틀리면 여기서 BadCredentialsException 등이 발생함)
            Authentication authentication = authenticationManager.authenticate(token);  // 여기서 UserService의 loadUserByUsername 메소드로 이동

            // 3. 인증 성공 시 로직
            AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();     // 현재 로그인한 사용자의 객체를 꺼내는 메소드
            String jwt = jwtUtil.createToken(user);
            ResponseCookie cookie = cookieUtil.createCookie(jwt);   // 쿠키 발급

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())    // JWT 토큰을 헤더로 설정해서 응답 (쿠키)
                    .body(UserDto.LoginRes.from(user));                    // 응답 결과로 로그인한 사용자 정보 같이 반환

        } catch (UsernameNotFoundException e) {
            // 4. 인증 실패 시
            // 4-1). 이메일이 없는 경우 (404 Not Found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.fail(BaseResponseStatus.USER_NOT_FOUND));

        } catch (BadCredentialsException e) {
            // 4-2). 비밀번호가 틀린 경우 (401 Unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.fail(BaseResponseStatus.PASSWORD_WRONG));

        } catch (AuthenticationException e) {
            // 4-3). 그 외 인증 오류 (401 Unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.fail(BaseResponseStatus.LOGIN_FAILED));
        }
    }

    // 로그인한 사용자 식별
    @Operation(summary = "현재 로그인 유저 정보 조회", description = "현재 인증된 사용자의 정보를 반환합니다.")
    @GetMapping("/me")
    public ResponseEntity getCurrentUser(@AuthenticationPrincipal AuthUserDetails user) {
        // Spring Security 필터에서 토큰 검증 후 user 객체를 채움
        if (user != null) {
            // 기존 로그인 응답과 동일한 DTO 형식을 사용
            return ResponseEntity.ok(UserDto.LoginRes.from(user));
        }

        // 토큰이 없거나 유효하지 않은 경우
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
    }

    // 로그아웃
    @Operation(summary = "로그아웃", description = "로그인 쿠키를 삭제하여 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity logout() {
        // 로그아웃 시 ATOKEN 쿠키에서 삭제
        ResponseCookie cookie = cookieUtil.removeCookie();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(Map.of("message", "로그아웃 성공."));  // 응답 결과로 로그인한 사용자 정보 같이 반환;
    }

}
