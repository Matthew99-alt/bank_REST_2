package com.example.bankcards.exception;

/**
 * Кастомное иксключение для случаев отрицательного баланса
 */
public class NegativeBalanceException extends RuntimeException {
    public NegativeBalanceException(String message) {
        super(message);
    }
}
