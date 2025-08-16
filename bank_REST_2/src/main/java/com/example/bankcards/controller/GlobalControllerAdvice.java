package com.example.bankcards.controller;

import com.example.bankcards.dto.ErrorDTO;
import com.example.bankcards.exception.DifferentIdentifierException;
import com.example.bankcards.exception.UnuniqueParameterException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * ControllerAdvice для корректного вывода сообщений об ошибках
 */

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(ex.getMessage());
        errorDTO.setNumber(HttpStatus.NOT_FOUND.value());
        errorDTO.setDescription(HttpStatus.NOT_FOUND.getReasonPhrase());

        return errorDTO;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }

    @ExceptionHandler({RuntimeException.class,
            DifferentIdentifierException.class,
            UnuniqueParameterException.class})
    public ErrorDTO handleRuntimeException(RuntimeException ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(ex.getMessage());
        errorDTO.setNumber(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDTO.setDescription(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        return errorDTO;
    }
}
