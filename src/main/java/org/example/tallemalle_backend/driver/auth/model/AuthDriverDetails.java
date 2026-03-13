package org.example.tallemalle_backend.driver.auth.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Getter
@Builder
public class AuthDriverDetails implements UserDetails {
    private Long idx;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private boolean enable;
    private String role;
    private String status;

    public static AuthDriverDetails from(Driver entity) {
        return AuthDriverDetails.builder()
                .idx(entity.getIdx())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .enable(entity.getEnable())
                .role(entity.getRole())
                .status(entity.getStatus())
                .build();
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