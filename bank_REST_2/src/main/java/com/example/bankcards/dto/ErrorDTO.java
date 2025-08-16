package com.example.bankcards.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO для вывода сообщения о ошибке
 */

@Getter
@Setter
public class ErrorDTO {
    private String message;
    private int number;
    private String description;
}
