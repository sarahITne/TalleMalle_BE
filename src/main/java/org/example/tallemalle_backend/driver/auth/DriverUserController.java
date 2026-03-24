package org.example.tallemalle_backend.driver.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "드라이버 회원가입",
            description = "이메일·비밀번호 및 본인인증 정보를 이용해 드라이버 계정을 등록합니다.\n반환: 생성된 드라이버 기본 정보(이름, 이메일, 닉네임 등)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(examples = @ExampleObject(value = "{\"idx\":12,\"email\":\"driver@tallemalle.com\",\"nickname\":\"김기사\"}"))
            ),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 본인인증 검증 실패")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody DriverDto.SignupReq dto) {
        DriverDto.SignupRes result =  driverUserService.signup(dto);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "드라이버 이메일 중복 확인", description = "회원가입 전 이메일 사용 가능 여부를 확인합니다. true면 사용 가능입니다.\n반환: boolean")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(examples = @ExampleObject(value = "true")))
    @GetMapping("/check-email")
    public ResponseEntity<?> emailCheck(
            @Parameter(description = "확인할 이메일", example = "driver@tallemalle.com")
            @RequestParam String email) {
        boolean available = driverUserService.emailCheck(email);
        return ResponseEntity.ok(available);
    }

    @Operation(summary = "드라이버 닉네임 중복 확인", description = "회원가입 전 닉네임 사용 가능 여부를 확인합니다. true면 사용 가능입니다.\n반환: boolean")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(examples = @ExampleObject(value = "true")))
    @GetMapping("/check-nickname")
    public ResponseEntity<?> nicknameCheck(
            @Parameter(description = "확인할 닉네임", example = "김기사")
            @RequestParam String nickname) {
        boolean available = driverUserService.nicknameCheck(nickname);
        return ResponseEntity.ok(available);
    }

    @Operation(
            summary = "드라이버 본인 인증",
            description = "PortOne identityVerificationId로 본인인증 결과를 조회합니다.\n반환: message, userInfo(name, phoneNumber, birth, gender)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "본인인증 성공",
                    content = @Content(examples = @ExampleObject(value = "{\"message\":\"본인인증 성공\",\"userInfo\":{\"name\":\"홍길동\",\"phoneNumber\":\"01012345678\",\"birth\":\"1999-01-01\",\"gender\":\"MALE\"}}"))
            ),
            @ApiResponse(responseCode = "400", description = "본인인증 실패 또는 잘못된 요청")
    })
    @PostMapping("/verify-identity")
    public ResponseEntity<?> verifyIdentity(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "PortOne 본인인증 요청 후 받은 identityVerificationId",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class), examples = @ExampleObject(
                            value = "{\"identityVerificationId\":\"iv_20260324_xxxxx\"}"
                    ))
            )
            @RequestBody Map<String, String> request) {
        String identityVerificationId = request.get("identityVerificationId");
        Map<String, Object> verified = driverUserService.confirmIdentity(identityVerificationId);
        if (verified == null || verified.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "본인인증 실패"));
        }
        return ResponseEntity.ok(Map.of("message", "본인인증 성공", "userInfo", verified));
    }

    @Operation(summary = "드라이버 로그인", description = "드라이버 계정으로 로그인하고 JWT 쿠키(ATOKEN)를 발급합니다.\n반환: 로그인된 드라이버 정보.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(examples = @ExampleObject(value = "{\"idx\":12,\"email\":\"driver@tallemalle.com\",\"role\":\"DRIVER\"}"))
            ),
            @ApiResponse(responseCode = "401", description = "비밀번호 오류 또는 인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
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

    @Operation(summary = "로그인한 드라이버 식별", description = "현재 ATOKEN 기준으로 로그인된 드라이버 정보를 반환합니다.\n반환: 로그인된 드라이버 정보.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(examples = @ExampleObject(value = "{\"idx\":12,\"email\":\"driver@tallemalle.com\",\"role\":\"DRIVER\"}"))),
            @ApiResponse(responseCode = "401", description = "인증 정보 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentDriver(@AuthenticationPrincipal AuthDriverDetails driver) {
        if (driver != null) {
            return ResponseEntity.ok(DriverDto.LoginRes.from(driver));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
    }

    @Operation(summary = "드라이버 로그아웃", description = "ATOKEN 쿠키를 만료시켜 로그아웃 처리합니다.\n반환: 로그아웃 성공 메시지.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(examples = @ExampleObject(value = "\"로그아웃 성공\"")))
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = cookieUtil.removeCookie();
        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body("로그아웃 성공");
    }

}
