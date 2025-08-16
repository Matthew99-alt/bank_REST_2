package com.example.bankcards.entity;

import com.example.bankcards.util.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Этот класс представляет данные таблицы card в виде объекта класса Card
 * Снабжён геттерами и сеттерами через Lombock
 * Является сущностью
 * Класс предоставляет данные о картах
 * имеет связь с таблицей card_user в виде "Многие к одному"
 */
@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "final_date", nullable = false)
    private LocalDate finalDate;

    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "balance", nullable = false)
    private Long balance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
