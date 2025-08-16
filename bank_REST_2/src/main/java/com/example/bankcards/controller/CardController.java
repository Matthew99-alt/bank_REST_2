package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для запросов связанных с катрами
*/

@RestController
@RequestMapping("/cards")
@CrossOrigin(origins = "http://localhost:8080")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/")
    public List<CardDTO> getAllCards() {
        return cardService.findAllCards();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public CardDTO getACard(@PathVariable("id") Long id) {
        return cardService.findCardById(id);
    }

    //С помощью этой операции пользователь узнаёт свой баланс и просматривает свою карту т.к. баланс - часть сущности Card
    @GetMapping("/userId")
    public List<CardDTO> getCardsByUserId(@RequestParam("id") Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return cardService.findByUserId(id, userDetails);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/")
    public CardDTO saveACard(@RequestBody @Valid CardDTO cardDTO) {
        return cardService.saveCard(cardDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/block")
    public CardDTO blockCard(@PathVariable("id") Long cardId) {
        return cardService.blockCard(cardId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/activate")
    public CardDTO activateCard(@PathVariable("id") Long cardId) {
        return cardService.activateCard(cardId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete") //указано в ТЗ, админ должен уметь активировать, блокировать, создавать и удалять карты
    public void deleteACard(@RequestParam("id") Long id) {
        cardService.deleteCard(id);
    }

    @PostMapping("/transfer")
    public TransactionDTO transfer(@RequestBody TransactionDTO transactionDTO, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return cardService.transfer(transactionDTO, userDetails);
    }
}
