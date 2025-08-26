package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "API для управления пользователями")
@SecurityRequirement(name = "bearerAuth") // Указываем, что нужен JWT токен
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    @Operation(summary = "Получить всех пользователей", description = "Получение списка всех пользователей (только для администраторов)")
    @ApiResponse(responseCode = "200", description = "Список пользователей получен")
    public List<UserDTO> getAllUsers() {
        return userService.findAllUsers();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/")
    @Operation(summary = "Получить пользователя по ID", description = "Получение информации о пользователе по идентификатору (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найдена", content = @Content)
    })
    public UserDTO getAUser(
            @Parameter(description = "ID пользователя", example = "123", required = true)
            @RequestParam("id") Long id) {
        return userService.findUserById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete")
    @Operation(summary = "Удалить пользователя", description = "Удаление пользователя по идентификатору (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    public void deleteAUser(
            @Parameter(description = "ID пользователя", example = "123", required = true)
            @RequestParam("id") Long id) {
        userService.deleteUser(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/edit")
    @Operation(summary = "Редактировать пользователя", description = "Обновление данных пользователя (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "400", description = "Невалидные данные", content = @Content)
    })
    public UserDTO editAUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные пользователя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "id": 123,
                          "firstName": "Иван",
                          "secondName": "Иванов",
                          "middleName": "Иванович",
                          "email": "ivan.new@example.com",
                          "password": "newpassword123",
                          "phoneNumber": "+79997654321",
                          "role": []
                        }
                        """
                            )
                    )
            )
            @RequestBody @Valid UserDTO userDTO) {
        return userService.editUser(userDTO);
    }
}