package com.example.bankcards.exception;

/**
 * Кастомное иксключение для случаев одинаковой карты
 */
public class SameCardException extends RuntimeException {
    public SameCardException(String message) {
        super(message);
    }
}
