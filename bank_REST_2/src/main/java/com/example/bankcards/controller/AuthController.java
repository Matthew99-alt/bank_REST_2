package com.example.bankcards.controller;


import com.example.bankcards.dto.*;
import com.example.bankcards.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public JwtResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }


    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerUser(@Valid @RequestBody UserDTO userDTO) {
        return authService.registerUser(userDTO);
    }
}
