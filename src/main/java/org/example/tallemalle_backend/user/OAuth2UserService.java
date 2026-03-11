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

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2UserService
        extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("서비스 코드 실행");

        String provider = userRequest
                .getClientRegistration().getRegistrationId();

        // OAuth2 로그인 실행
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 내 서비스의 DTO로 변환
        UserDto.OAuth dto = UserDto.OAuth.from(oAuth2User.getAttributes(), provider);
        // DB에 회원이 있나 없나 확인
        Optional<User> result = userRepository.findByEmail(dto.getEmail());
        // 없으면 가입 시켜주기
        if (!result.isPresent()) {
            User user = userRepository.save(dto.toEntity());
            return AuthUserDetails.from(user);
        }
        else {
            User user = result.get();
            return AuthUserDetails.from(user);
        }
    }
}
