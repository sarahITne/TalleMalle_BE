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
    private String username;
    private String password;
    private boolean enable;
    private String role;
    private String name;
    private Map<String, Object> attributes;

    public static AuthUserDetails from(User entity) {
        return AuthUserDetails.builder()
                .idx(entity.getIdx())
                .username(entity.getEmail())
                .name(entity.getName())
                .password(entity.getPassword())
                .enable(entity.isEnable())
                .role(entity.getRole())
                .build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

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
        return username;
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

    public User toEntity() {
        return User.builder()
                .idx(this.idx)
                .email(this.username)
                .password(this.password)
                .enable(this.enable)
                .role(this.role)
                .build();
    }

}
