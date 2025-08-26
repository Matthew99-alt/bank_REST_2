package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserDetailsImpl;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Карты", description = "API для управления банковскими картами")
@SecurityRequirement(name = "bearerAuth") // Указываем, что нужен JWT токен
public class CardController {

    private final CardService cardService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/")
    @Operation(summary = "Получить все карты", description = "Получение списка всех карт (только для администраторов)")
    @ApiResponse(responseCode = "200", description = "Список карт получен")
    public List<CardDTO> getAllCards() {
        return cardService.findAllCards();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Получить карту по ID", description = "Получение информации о конкретной карте по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта найдена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content)
    })
    public CardDTO getACard(
            @Parameter(description = "ID карты", example = "1", required = true)
            @PathVariable("id") Long id) {
        return cardService.findCardById(id);
    }

    @GetMapping("/userId")
    @Operation(summary = "Получить карты пользователя", description = "Получение списка карт конкретного пользователя")
    @ApiResponse(responseCode = "200", description = "Список карт получен")
    public List<CardDTO> getCardsByUserId(
            @Parameter(description = "ID пользователя", example = "123", required = true)
            @RequestParam("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cardService.findByUserId(id, userDetails);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новую карту", description = "Создание новой банковской карты (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Карта успешно создана"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные", content = @Content)
    })
    public CardDTO saveACard(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания карты",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "finalDate": "2025-12-31",
                                              "status": "ACTIVE",
                                              "balance": 100000,
                                              "userId": 123
                                            }
                                            """
                            )
                    )
            )
            @RequestBody @Valid CardDTO cardDTO) {
        return cardService.saveCard(cardDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/block")
    @Operation(summary = "Заблокировать карту", description = "Блокировка карты по идентификатору (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта заблокирована"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content)
    })
    public CardDTO blockCard(
            @Parameter(description = "ID карты", example = "1", required = true)
            @PathVariable("id") Long cardId) {
        return cardService.blockCard(cardId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Активировать карту", description = "Активация заблокированной карты (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта активирована"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content)
    })
    public CardDTO activateCard(
            @Parameter(description = "ID карты", example = "1", required = true)
            @PathVariable("id") Long cardId) {
        return cardService.activateCard(cardId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete")
    @Operation(summary = "Удалить карту", description = "Удаление карты по идентификатору (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта удалена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена", content = @Content)
    })
    public void deleteACard(
            @Parameter(description = "ID карты", example = "1", required = true)
            @RequestParam("id") Long id) {
        cardService.deleteCard(id);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Перевод средств", description = "Перевод денежных средств между картами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод выполнен успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка перевода (недостаточно средств и т.д.)", content = @Content)
    })
    public TransactionDTO transfer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для перевода",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "fromCardId": 1,
                                              "toCardId": 2,
                                              "amount": 5000,
                                              "description": "Перевод за услуги"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody TransactionDTO transactionDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cardService.transfer(transactionDTO, userDetails);
    }

    @GetMapping("/search")
    public Page<CardDTO> searchCards(
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "final_date", required = false) LocalDate finalDate,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return cardService.search(userId, status, finalDate, pageable);
    }
}