package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.UnuniqueParameterException;
import com.example.bankcards.repository.UsersRepository;
import com.example.bankcards.util.RoleEnum;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.util.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.bankcards.util.RoleEnum;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }


    public UserDTO registerUser(@Valid @RequestBody UserDTO userDTO) {

        if (userService.existsByEmail(userDTO.getEmail())
                || userService.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new UnuniqueParameterException("Email and phone number should be unique");
        }

        Set<Role> strRoles = userDTO.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            Set<RoleEnum> rolesToFind = strRoles.stream()
                    .map(role -> {
                        if (role.getName().equals(RoleEnum.ROLE_ADMIN)) return RoleEnum.ROLE_ADMIN;
                        return RoleEnum.ROLE_USER;
                    })
                    .collect(Collectors.toSet());

            List<Role> foundRoles = roleRepository.findByNameIn(rolesToFind);

            if (foundRoles.size() != rolesToFind.size()) {
                throw new RuntimeException("Error: Some roles were not found.");
            }

            roles.addAll(foundRoles);
        }

        userDTO.setRole(roles);
        userDTO.setPassword(encoder.encode(userDTO.getPassword()));



        return userService.saveUser(userDTO);
    }
}
