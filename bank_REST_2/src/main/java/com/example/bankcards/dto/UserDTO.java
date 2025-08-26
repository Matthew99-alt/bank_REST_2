package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Schema(description = "Данные пользователя")
public class UserDTO {

    @Schema(description = "Уникальный идентификатор пользователя", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван", minLength = 2, maxLength = 50)
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Иванов", minLength = 2, maxLength = 50)
    private String secondName;

    @Schema(description = "Отчество пользователя", example = "Иванович", minLength = 2, maxLength = 50)
    private String middleName;

    @Schema(description = "Email адрес", example = "user@example.com")
    private String email;

    @Schema(description = "Пароль (минимум 6 символов)", example = "password123", minLength = 6)
    private String password;

    @Schema(description = "Номер телефона", example = "+79991234567")
    private String phoneNumber;

    @Schema(description = "Роли пользователя")
    private Set<Role> role;
}