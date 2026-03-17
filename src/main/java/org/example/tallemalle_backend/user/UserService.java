package org.example.tallemalle_backend.user;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.driver.auth.DriverUserRepository;
import org.example.tallemalle_backend.driver.auth.model.AuthDriverDetails;
import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.EmailVerify;
import org.example.tallemalle_backend.user.model.User;
import org.example.tallemalle_backend.user.model.UserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.example.tallemalle_backend.common.model.BaseResponseStatus.SIGNUP_DUPLICATE_EMAIL;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final DriverUserRepository driverUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerifyRepository emailVerifyRepository;

    // 회원가입
    public UserDto.SignupRes signup(UserDto.SignupReq dto) {

        // 1. 이메일 중복 확인
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw BaseException.from(SIGNUP_DUPLICATE_EMAIL);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. DTO를 엔티티로 변환 후 저장
        User user = dto.toEntity(encodedPassword);
        userRepository.save(user);  // 저장 후 user에 idx 세팅됨

        // 4. 이메일 인증 메일 보내기
        String uuid = UUID.randomUUID().toString();     // 이메일 인증에 사용할 고유 토큰 (UUID) 생성
        emailService.sendWelcomeMail(uuid, dto.getEmail());

        // 4-1. 이메일 전송 내역 저장
        EmailVerify emailVerify =
                EmailVerify.builder()
                        .email(dto.getEmail())
                        .uuid(uuid)
                        .build();

        emailVerifyRepository.save(emailVerify);

        return UserDto.SignupRes.from(user);
    }

    // 이메일 중복 확인
    public boolean emailCheck(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false;   // 중복된 이메일
        } else {
            return true;    // 사용 가능한 이메일
        }
    }

    // 닉네임 중복 확인
    public boolean nicknameCheck(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            return false;   // 중복된 닉네임
        } else {
            return true;    // 사용 가능한 닉네임
        }
    }


    // 이메일 인증 및 처리
    public void verify(String uuid) {
        // 1. uuid로 이메일 찾기
        EmailVerify emailVerify = emailVerifyRepository.findByUuid(uuid).orElseThrow();

        // 2. 이메일로 user 찾기
        User user = userRepository.findByEmail(emailVerify.getEmail()).orElseThrow();

        // 3. 해당 유저의 enable을 true로 update
        user.setEnable(true);
        userRepository.save(user);
    }


    // 소셜 로그인 사용자 추가 정보 업데이트
    @Transactional
    public UserDto.ExtraInfoRes extraInfo(String email, UserDto.ExtraInfoReq dto) {
        // 1. 전달 받은 email로 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 2. 유저 정보 업데이트 (Dirty Checking으로 자동 저장됨)
        user.updateExtraInfo(
                dto.getNickname(),
                dto.getPhoneNumber(),
                dto.getBirth(),
                dto.getGender()
        );

        return UserDto.ExtraInfoRes.from(user);
    }


    // 로그인
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 일반 유저 (USER_) 인 경우
        if (username.startsWith("USER_")) {
            String realEmail = username.substring(5);

            User user = userRepository.findByEmail(realEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다: " + realEmail));

            return AuthUserDetails.from(user);
        }

        // 2. 기사 (DRIVER_) 인 경우
        else if (username.startsWith("DRIVER_")) {
            String realEmail = username.substring(7);

            Driver driver = driverUserRepository.findByEmail(realEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("해당 기사를 찾을 수 없습니다: " + realEmail));

            return AuthDriverDetails.from(driver);
        }
        else {
            throw new UsernameNotFoundException("유효하지 않은 로그인 타입입니다: " + username);
        }

    }
}