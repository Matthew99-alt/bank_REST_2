package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.UsersRepository;
import com.example.bankcards.util.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Маппер, отдельный класс для выполнения операций по переводу сущности карт в DTO и наоборот
*/

@Component
@RequiredArgsConstructor
public class CardMapper {

    private final UsersRepository UsersRepository;

    public Card makeACard(CardDTO cardDTO) {
        Card card = new Card();

        card.setId(cardDTO.getId());
        card.setBalance(cardDTO.getBalance());
        if (cardDTO.getStatus() != null) {
            try {
                Status status = Status.valueOf(cardDTO.getStatus().toUpperCase());
                card.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid card status: " + cardDTO.getStatus());
            }
        }
        card.setFinalDate(cardDTO.getFinalDate());
        card.setUser(UsersRepository.getReferenceById(cardDTO.getUserId()));

        return card;
    }

    public CardDTO makeACardDTO(Card card) {
        CardDTO cardDTO = new CardDTO();

        cardDTO.setId(card.getId());
        cardDTO.setBalance(card.getBalance());
        cardDTO.setStatus(card.getStatus().toString());
        cardDTO.setFinalDate(card.getFinalDate());
        cardDTO.setUserId(card.getUser().getId());

        return cardDTO;
    }
}
