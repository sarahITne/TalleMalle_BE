package org.example.tallemalle_backend.driver.auth;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.driver.auth.model.AuthDriverDetails;
import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.example.tallemalle_backend.driver.auth.model.DriverDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.example.tallemalle_backend.common.model.BaseResponseStatus.SIGNUP_DUPLICATE_EMAIL;

@RequiredArgsConstructor
@Service
public class DriverUserService  {
    private final DriverUserRepository driverUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverDto.SignupRes signup(DriverDto.SignupReq dto) {

        // 1. 이메일 중복 확인
        if(driverUserRepository.findByEmail(dto.getEmail()).isPresent()){
            throw BaseException.from(SIGNUP_DUPLICATE_EMAIL);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 3. DTO를 엔티티로 변환 후 저장
        Driver user = dto.toEntity(encodedPassword);
        driverUserRepository.save(user);  // 저장 후 user에 idx 세팅됨

        return DriverDto.SignupRes.from(user);
    }


}
