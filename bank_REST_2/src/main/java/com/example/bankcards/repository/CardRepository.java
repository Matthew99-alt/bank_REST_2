package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий отвечающий за работу с таблицей указанной в сущности Card
 * @see  Card
*/

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Card findByUserId(Long userId);
}

