package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO для обработки информации о картах
*/


@Getter
@Setter
public class CardDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate finalDate;

    private String status;

    private Long balance;

    private Long userId;

}
