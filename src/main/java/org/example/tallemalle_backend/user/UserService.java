package org.example.tallemalle_backend.user;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.driver.auth.DriverUserRepository;
import org.example.tallemalle_backend.driver.auth.model.AuthDriverDetails;
import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.example.tallemalle_backend.user.model.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.example.tallemalle_backend.common.model.BaseResponseStatus.SIGNUP_DUPLICATE_EMAIL;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final DriverUserRepository driverUserRepository;
    private final PasswordEncoder passwordEncoder;

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

        return UserDto.SignupRes.from(user);
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