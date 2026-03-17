package org.example.tallemalle_backend.config.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tallemalle_backend.user.model.AuthUserDetails;
import org.example.tallemalle_backend.utils.JwtUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtUtil jwtUtil;
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest serverHttpRequest) {
            HttpServletRequest httpReq = serverHttpRequest.getServletRequest();
            if (httpReq.getCookies() != null) {
                for (Cookie cookie : httpReq.getCookies()) {
                    if (cookie.getName().equals("ATOKEN")) {
                        try {
                            Long idx = jwtUtil.getUserIdx(cookie.getValue());
                            String email = jwtUtil.getEmail(cookie.getValue());
                            String role = jwtUtil.getRole(cookie.getValue());

                            AuthUserDetails user = AuthUserDetails.builder()
                                    .idx(idx)
                                    .email(email)
                                    .role(role)
                                    .build();

                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(new SimpleGrantedAuthority(role))
                            );
                            attributes.put("user", authentication);

                            return true;
                        } catch (JwtException | IllegalArgumentException e) {
                            log.warn("웹소켓 핸드셰이크 거부: 유효하지 않거나 만료된 ATOKEN, reason={}", e.getMessage());
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            return false;
                        }
                    }
                }
            }

        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
