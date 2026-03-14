package org.example.tallemalle_backend.user;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

// 소셜 로그인 성공 후 "성공 처리" 로직 (사용자 정보 받아오기 등)
@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {    // userRequest가 요청 dto, 안에 들어있는 내용은 우리가 yml 파일에 설정해준 내용

        // 소셜 로그인 했을 때 어떤 소셜 로그인으로 했는 지 받아오기 (= provider)
        String provider = userRequest.getClientRegistration().getRegistrationId();  // yml 파일에 설정해 놓은 정보 가져오기

        // 요청 dto(userRequest) 가지고 Oauth2 로그인 실행
        // 카카오 로그인 실패하면 여기로 코드가 안타짐 (그래서 아래 코드가 로그인 성공 로직이라고 생각하면 됨)
        OAuth2User oAuth2User = super.loadUser(userRequest);    // 부모 클래스에 있던 메소드 그대로 실행

        // 사용자 정보 가져오기 (나중에 DTO로 바꿀 것)
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = ((Long) attributes.get("id")).toString();

        // 이메일 확인
        String email = providerId + "@kakao.social";    // 소셜 로그인한 사용자의 이메일 형식 맞춰줌 (소셜 로그인 구분)
        Map properties = (Map) attributes.get("properties");
        String name = (String) properties.get("nickname");

        // DB에 회원이 있나 없나 확인
        Optional<User> result = userRepository.findByEmail(email);

        // 없으면 가입 시켜 주기
        if (!result.isPresent()) {
            User user = userRepository.save(
                    User.builder()
                            .email(email)
                            .password("kakao-social-login")
                            .name(name)
                            .nickname("임시 닉네임")          // 임시 닉네임 (카카오에서 받아올 수 없음, 이후 추가 회원가입 단계에서 정보 입력)
                            .phoneNumber("010-0000-0000")   // 임시 번호
                            .birth(LocalDate.parse("1900-01-01"))    // 임시 생년월일
                            .gender("PENDING")    // 임시 성별 : 대기중
                            .provider(provider)
                            .role("'ROLE_GUEST'")   // 권한으로 추가 정보 대상자 구분
                            .build()
            );

            return AuthUserDetails.from(user);
        }
        // 있으면 해당 사용자 반환
        else {
            User user = result.get();

            return AuthUserDetails.from(user);
        }
    }
}
