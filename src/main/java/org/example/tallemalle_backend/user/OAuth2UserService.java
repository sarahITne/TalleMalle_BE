package org.example.tallemalle_backend.user;

import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.user.model.User;
import org.example.tallemalle_backend.user.model.UserDto;
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

        // 사용자 정보 가져오기
        UserDto.OAuth dto = UserDto.OAuth.from(oAuth2User.getAttributes(), provider);

        // DB에 회원이 있나 없나 확인
        Optional<User> result = userRepository.findByEmail(dto.getEmail());

        // 없으면 가입 시켜 주기
        if (!result.isPresent()) {
            User user = userRepository.save(dto.toEntity());

            return AuthUserDetails.from(user);
        }
        // 있으면 해당 사용자 반환
        else {
            User user = result.get();
            return AuthUserDetails.from(user);
        }
    }
}
