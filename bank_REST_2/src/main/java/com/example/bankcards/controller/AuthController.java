package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для входа и регистрации пользователей")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    @Operation(summary = "Вход пользователя", description = "Аутентификация существующего пользователя по email и паролю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные", content = @Content),
            @ApiResponse(responseCode = "400", description = "Невалидные данные запроса", content = @Content)
    })
    public JwtResponse authenticateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для входа",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "email": "user@example.com",
                          "password": "password123"
                        }
                        """
                            )
                    )
            )
            @Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация пользователя", description = "Создание нового аккаунта пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные данные или email уже существует", content = @Content)
    })
    public UserDTO registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового пользователя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "firstName": "Иван",
                          "secondName": "Иванов",
                          "middleName": "Иванович",
                          "email": "ivan@example.com",
                          "password": "password123",
                          "phoneNumber": "+79991234567",
                          "role": [{"id": 2,"name": "ROLE_ADMIN"}]
                        }
                        """
                            )
                    )
            )
            @Valid @RequestBody UserDTO userDTO) {
        return authService.registerUser(userDTO);
    }
}