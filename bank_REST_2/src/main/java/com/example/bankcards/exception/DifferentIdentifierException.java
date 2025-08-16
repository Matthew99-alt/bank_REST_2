package com.example.bankcards.exception;

/**
 * Кастомное иксключение для случаев перевода на карты разных пользователей
 */

public class DifferentIdentifierException extends RuntimeException {
    public DifferentIdentifierException(String message) {
        super(message);
    }
}
