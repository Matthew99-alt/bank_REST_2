package com.example.bankcards.util;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;

/**
 * Класс с прописанными значаниями статуса карт
*/

public enum Status {
    ACTIVE, BLOCKED, OUTDATED
}
