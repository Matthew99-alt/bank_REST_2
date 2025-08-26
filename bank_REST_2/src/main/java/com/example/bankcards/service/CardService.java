package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.DifferentIdentifierException;
import com.example.bankcards.exception.NegativeBalanceException;
import com.example.bankcards.exception.SameCardException;
import com.example.bankcards.exception.UnactiveCardException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.Status;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Класс сервис отвечающий за реализацию запросов контроллера и работу с объектами класса Card
 *
 * @see Card
 * @see CardRepository
 * @see CardDTO
 * @see CardMapper
 * @see com.example.bankcards.controller.CardController
 */

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Transactional(readOnly = true)
    public List<CardDTO> findAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(cardMapper::makeACardDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CardDTO findCardById(Long encodedId) {
        Card card = cardRepository.findById(encodedId)
                .orElseThrow(() -> new EntityNotFoundException("Указанная карта не найдена"));
        return cardMapper.makeACardDTO(card);
    }

    @Transactional(readOnly = true)
    public List<CardDTO> findByUserId(Long userId, UserDetailsImpl userDetails) {

        if (!userDetails.getId().equals(userId)) {
            throw new DifferentIdentifierException("Идентификатор пользователя и владельца карты разные. В доступе отказано");
        }

        return cardRepository.findAllByUserId(userId)
                .stream()
                .map(cardMapper::makeACardDTO)
                .toList();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CardDTO saveCard(CardDTO cardDTO) {

        if (cardDTO.getBalance() < 0) {
            throw new NegativeBalanceException("Недостаточно средств");
        }

        Card savedCard = cardRepository.save(cardMapper.makeACard(cardDTO));
        return cardMapper.makeACardDTO(savedCard);
    }

    @Transactional
    public CardDTO blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow();
        card.setStatus(Status.BLOCKED);
        return cardMapper.makeACardDTO(cardRepository.save(card));
    }

    @Transactional
    public CardDTO activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow();
        card.setStatus(Status.ACTIVE);
        return cardMapper.makeACardDTO(cardRepository.save(card));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransactionDTO transfer(TransactionDTO transactionDTO, UserDetailsImpl userDetails) {

        Card getFromCard = cardRepository.getReferenceById(transactionDTO.fromCardId());
        Card getToCard = cardRepository.getReferenceById(transactionDTO.toCardId());

        if (!getFromCard.getUser().getId().equals(userDetails.getId())) {
            throw new DifferentIdentifierException("Введен не верный идентификатор");
        }
        if (!getToCard.getUser().getId().equals(userDetails.getId())) {
            throw new DifferentIdentifierException("Введен не верный идентификатор");
        }

        if (transactionDTO.amount() < 0) {
            throw new NegativeBalanceException("Amount should be more than zero");
        }
        if (getFromCard.getStatus() != Status.ACTIVE || getToCard.getStatus() != Status.ACTIVE) {
            throw new UnactiveCardException("Both cards must be active for transaction");
        }
        if (Objects.equals(getToCard.getId(), getFromCard.getId())) {
            throw new SameCardException("The cards for transaction are the same");
        }

        getFromCard.setBalance(getFromCard.getBalance() - transactionDTO.amount());
        getToCard.setBalance(getToCard.getBalance() + transactionDTO.amount());

        cardRepository.save(getFromCard);

        cardRepository.save(getToCard);

        return transactionDTO;
    }

    @Transactional
    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }

    @Transactional
    public Page<CardDTO> search(Long userId, String status, LocalDate finalDate, Pageable pageable) {
        Specification<Card> spec = Specification.where(null);

        if (userId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("user").get("id"), userId));
        }
        if (status != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (finalDate != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("finalDate"), finalDate));
        }
        if (userId == null && status == null && finalDate == null){
            throw new EntityNotFoundException("Для поиска необходимо заполнить хотя бы один из трёх параметров");
        }

        return cardRepository.findAll(spec, pageable).map(cardMapper::makeACardDTO);
    }
}
