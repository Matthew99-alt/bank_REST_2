package com.example.bankcards.dto;

/**
 * DTO для перевода денег между картами
 */
public record TransactionDTO(
        Long fromCardId,
        Long toCardId,
        Long amount
) {
}
