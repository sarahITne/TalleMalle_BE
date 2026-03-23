package org.example.tallemalle_backend.driver.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.driver.auth.model.DriverDto;
import org.example.tallemalle_backend.driver.auth.model.AuthDriverDetails;
import org.example.tallemalle_backend.utils.JwtUtil;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/driver")
@RestController
@RequiredArgsConstructor
public class DriverUserController {
    private final DriverUserService driverUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody DriverDto.SignupReq dto) {
        DriverDto.SignupRes result =  driverUserService.signup(dto);

        return ResponseEntity.ok(result);
    }


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody DriverDto.LoginReq dto) {
        String prefixedEmail = "DRIVER_" + dto.getEmail();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(prefixedEmail, dto.getPassword(), null);

        Authentication authentication = authenticationManager.authenticate(token);  // 여기서 UserService의 loadUserByUsername 메소드로 이동
        AuthDriverDetails driver = (AuthDriverDetails) authentication.getPrincipal();     // 현재 로그인한 사용자의 객체를 꺼내는 메소드

        if (!"DRIVER".equals(driver.getRole())) {
            throw BaseException.from(BaseResponseStatus.DRIVER_ROLE_REQUIRED);
        }

        if(driver != null) {
            String jwt = jwtUtil.createToken(driver);
            return ResponseEntity.ok()
                    .header("Set-Cookie", "ATOKEN=" + jwt + "; path=/")   // JWT 토큰을 헤더로 설정해서 응답 (쿠키)
                    .body(DriverDto.LoginRes.from(driver));  // 응답 결과로 로그인한 사용자 정보 같이 반환
        }

        return ResponseEntity.ok("로그인 실패");
    }

}
