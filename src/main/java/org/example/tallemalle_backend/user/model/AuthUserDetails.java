package org.example.tallemalle_backend.user.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@Getter
@Builder
public class AuthUserDetails implements UserDetails, OAuth2User {
    private Long idx;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private boolean enable;
    private String role;
    private UserStatus status;
    private Map<String, Object> attributes;

    public static AuthUserDetails from(User entity) {
        return AuthUserDetails.builder()
                .idx(entity.getIdx())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .name(entity.getName())
                .nickname(entity.getProfile() != null ? entity.getProfile().getNickname() : null)
                .enable(entity.getEnable())
                .role(entity.getRole())
                .status(entity.getStatus())
                .build();
    }

    public User toEntity() {
        return User.builder()
                .idx(this.idx)
                .email(this.email)
                .password(this.password)
                .name(this.name)
                .enable(this.enable)
                .role(this.role)
                .status(this.status)
                .build();
    }

    // OAuth2User 관련 오버라이드
    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    // UserDetails 관련 오버라이드
    @Override
    public boolean isEnabled() {
        return enable;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
