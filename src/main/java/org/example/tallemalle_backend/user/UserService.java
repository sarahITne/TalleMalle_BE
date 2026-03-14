package org.example.tallemalle_backend.user;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.EmailVerify;
import org.example.tallemalle_backend.user.model.User;
import org.example.tallemalle_backend.user.model.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.example.tallemalle_backend.common.model.BaseResponseStatus.SIGNUP_DUPLICATE_EMAIL;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerifyRepository emailVerifyRepository;

    // 회원가입
    public UserDto.SignupRes signup(UserDto.SignupReq dto) {

        // 1. 이메일 중복 확인
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
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

    // 로그인
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 이메일로 유저 조회
        User user = userRepository.findByEmail(username).orElseThrow();

        return AuthUserDetails.from(user);
    }

}
