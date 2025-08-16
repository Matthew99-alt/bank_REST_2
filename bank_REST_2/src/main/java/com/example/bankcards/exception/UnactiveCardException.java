package com.example.bankcards.exception;

/**
 * Кастомное иксключение для случаев действия с неактивными картами
 */

public class UnactiveCardException extends RuntimeException {
    public UnactiveCardException(String message) {
        super(message);
    }
}
