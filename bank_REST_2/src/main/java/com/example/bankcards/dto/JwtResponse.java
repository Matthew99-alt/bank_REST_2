package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Ответ с JWT токеном после успешной аутентификации")
public class JwtResponse {

    @Schema(description = "JWT токен доступа",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String token;

    @Schema(description = "Тип токена", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";

    @Schema(description = "ID пользователя", example = "123")
    private Long id;

    @Schema(description = "Имя пользователя", example = "ivan_ivanov")
    private String username;

    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Список ролей пользователя", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }
}