package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Этот класс представляет данные таблицы card_user в виде объекта класса CardUser
 * Класс предоставляет данные о пользователях карт
 * Снабжён геттерами и сеттерами через Lombock
 * Является сущностью
 */

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "second_name", nullable = false)
    private String secondName;

    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> role = new HashSet<>();

}
