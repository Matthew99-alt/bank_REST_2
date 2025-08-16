package com.example.bankcards.exception;

/**
 * Кастомное иксключение для случаев ввода уже существующеего email'а или номера телефона
 */

public class UnuniqueParameterException extends RuntimeException {
    public UnuniqueParameterException(String message) {
        super(message);
    }
}
