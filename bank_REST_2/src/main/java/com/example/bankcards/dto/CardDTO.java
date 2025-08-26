package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Данные банковской карты")
public class CardDTO {

    @Schema(description = "Уникальный идентификатор карты", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата окончания действия карты", example = "2025-12-31")
    private LocalDate finalDate;

    @Schema(description = "Статус карты", example = "ACTIVE",
            allowableValues = {"ACTIVE", "BLOCKED", "EXPIRED"})
    private String status;

    @Schema(description = "Баланс карты в копейках", example = "150000")
    private Long balance;

    @Schema(description = "ID владельца карты", example = "123")
    private Long userId;
}