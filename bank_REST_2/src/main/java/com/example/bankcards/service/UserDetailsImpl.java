package com.example.bankcards.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {
    private final Long id;

    private final String username;

    private final String email;

    private final String password;

    private final String phoneNumber;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
