package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для перевода денег между картами
 */
@Schema(description = "Данные для перевода средств между картами")
public record TransactionDTO(

        @NotNull
        @Schema(description = "ID карты отправителя", example = "1")
        Long fromCardId,

        @NotNull
        @Schema(description = "ID карты получателя", example = "2")
        Long toCardId,

        @NotNull
        @Min(1)
        @Schema(description = "Сумма перевода в копейках (минимум 1)", example = "5000")
        Long amount
) {
}