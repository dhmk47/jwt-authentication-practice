package com.jwtpractice.service;

import com.jwtpractice.domain.user.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class PrincipalDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorityColleciton = new ArrayList<>();

        user.getUserRoles().forEach(role -> {
            grantedAuthorityColleciton.add(() -> role);
        });

        return grantedAuthorityColleciton;
    }

    @Override
    public String getPassword() {
        return user.getUser_password();
    }

    @Override
    public String getUsername() {
        return user.getUser_id();
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}