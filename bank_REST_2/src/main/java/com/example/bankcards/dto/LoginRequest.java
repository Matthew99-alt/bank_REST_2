package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Запрос на аутентификацию")
public class LoginRequest {

    @NotBlank
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Пароль пользователя", example = "myPassword123")
    private String password;
}