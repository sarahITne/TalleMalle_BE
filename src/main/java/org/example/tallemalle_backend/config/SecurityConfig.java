package org.example.tallemalle_backend.config;


import lombok.RequiredArgsConstructor;
import org.example.tallemalle_backend.config.filter.JwtFilter;
import org.example.tallemalle_backend.config.oauth2.OAuth2AuthenticationSuccessHandler;
import org.example.tallemalle_backend.config.oauth2.OAuth2AuthorizationRequestRepository;
import org.example.tallemalle_backend.user.OAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final OAuth2UserService oAuth2UserService;  // 소셜 로그인 관련 Service 클래스
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    @Value("${app.front-url}")
    private String frontUrl;

    // 비밀번호 인코딩을 위한 객체
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {
        // 1. CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 2. JWT를 쓰기 위해 기본 보안 기능을 모두 끄기
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        // 3. 스프링 시큐리티 기본 세션 로그인 방식 꺼주기 (OAuth 로그인 관련)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));    // 즉, 상태 저장할 필요 없다는 뜻


        // 4. OAuth2 로그인 기능 설정
        http.oauth2Login(config -> {
            config.authorizationEndpoint(endpoint
                    -> endpoint.authorizationRequestRepository(oAuth2AuthorizationRequestRepository)
            );

            // 사용자 정보를 받아오는 코드를 어떤 클래스에 짤건지 지정하는 부분 (-> OAuth2UserService 클래스)
            config.userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService));
            // OAuth2 인증 과정이 성공했을 때 실행될 커스텀 핸들러
            config.successHandler(oAuth2AuthenticationSuccessHandler);
        });

        // 5. 권한 설정 (인가)
        http.authorizeHttpRequests(
                (auth) -> auth
                        // 접근 제어
                        // 1. 1. 공용 인프라 및 설정 (CORS, Swagger, WebSocket)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/ws/**").permitAll()

                        // 2. 공지사항 (Read는 전체 허용, Write/Update/Delete는 인증 필요)
                        .requestMatchers(HttpMethod.GET, "/notices/**").permitAll()
                        .requestMatchers("/notices/**").authenticated()

                        // 3. 예외적인 인증 필요 경로 (중요: 전체 허용 패턴보다 위에 위치해야 함)
                        .requestMatchers("/user/me", "/user/signup/extra", "/driver/me").authenticated()

                        // 4. 사용자(User) 및 드라이버(Driver) 관련 인증 절차
                        // 회원가입, 로그인, 중복체크, 본인인증 등은 모두 비로그인 허용
                        .requestMatchers("/user/**", "/driver/**").permitAll()

                        // 5. 나머지 모든 API는 기본적으로 인증 필요
                        .anyRequest().authenticated()
        );

        // 6. JWT 필터 추가
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // CORS 설정 정보
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("====== [CHECK] Configured Front URL: '" + frontUrl + "' ======");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(frontUrl)); // 프론트엔드 주소
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 대해 CORS 적용
        return source;
    }
}
