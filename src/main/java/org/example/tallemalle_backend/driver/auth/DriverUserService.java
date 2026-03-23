package org.example.tallemalle_backend.driver.auth;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.common.exception.BaseException;
import org.example.tallemalle_backend.common.model.BaseResponseStatus;
import org.example.tallemalle_backend.driver.auth.model.Driver;
import org.example.tallemalle_backend.driver.auth.model.DriverDto;
import org.example.tallemalle_backend.user.IdentityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

import static org.example.tallemalle_backend.common.model.BaseResponseStatus.SIGNUP_DUPLICATE_EMAIL;

@RequiredArgsConstructor
@Service
public class DriverUserService  {
    private final DriverUserRepository driverUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdentityService identityService;

    public DriverDto.SignupRes signup(DriverDto.SignupReq dto) {
        validateIdentity(dto);

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

    public boolean emailCheck(String email) {
        return driverUserRepository.findByEmail(email).isEmpty();
    }

    public boolean nicknameCheck(String nickname) {
        return driverUserRepository.findByNickname(nickname).isEmpty();
    }

    public Map<String, Object> confirmIdentity(String identityVerificationId) {
        return identityService.confirmIdentity(identityVerificationId);
    }

    private void validateIdentity(DriverDto.SignupReq dto) {
        Map<String, Object> verified = identityService.confirmIdentity(dto.getIdentityVerificationId());
        if (verified == null || verified.isEmpty()) {
            throw BaseException.from(BaseResponseStatus.REQUEST_ERROR);
        }

        String verifiedName = normalizeName(readString(verified, "name"));
        String verifiedPhone = normalizePhone(readString(verified, "phoneNumber", "phone_number", "phone"));
        String verifiedBirth = normalizeDate(readString(verified, "birthDate", "birth", "birthday"));

        String reqName = normalizeName(dto.getName());
        String reqPhone = normalizePhone(dto.getPhoneNumber());
        String reqBirth = dto.getBirth() != null ? dto.getBirth().toString() : null;

        boolean isNameMatched = verifiedName != null && verifiedName.equals(reqName);
        boolean isPhoneMatched = verifiedPhone != null && verifiedPhone.equals(reqPhone);
        boolean isBirthMatched = verifiedBirth != null && verifiedBirth.equals(reqBirth);

        if (!isNameMatched || !isPhoneMatched || !isBirthMatched) {
            throw BaseException.from(BaseResponseStatus.REQUEST_ERROR);
        }
    }

    private String readString(Map<String, Object> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    private String normalizeName(String value) {
        return value == null ? null : value.replaceAll("\\s+", "");
    }

    private String normalizePhone(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.length() == 11) {
            return digits.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        }
        if (digits.length() == 10) {
            return digits.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        }
        return value;
    }

    private String normalizeDate(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.length() != 8) {
            return value;
        }
        return LocalDate.of(
                Integer.parseInt(digits.substring(0, 4)),
                Integer.parseInt(digits.substring(4, 6)),
                Integer.parseInt(digits.substring(6, 8))
        ).toString();
    }


}
