package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDetailsFactory {
    public UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getFirstName()+" "+user.getSecondName()+" "+user.getMiddleName(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getPhoneNumber()
        );
    }
}