package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.DifferentIdentifierException;
import com.example.bankcards.exception.NegativeBalanceException;
import com.example.bankcards.exception.SameCardException;
import com.example.bankcards.exception.UnactiveCardException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.RoleEnum;
import com.example.bankcards.util.Status;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    //findAllCardsTest
    @Test
    void findAllCardsTest() {
        User user = makeAUser();
        user.setId(1L);

        Card card = makeACard(user);
        card.setId(123L);

        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(card.getId());

        when(cardRepository.findAll()).thenReturn(List.of(card));
        when(cardMapper.makeACardDTO(card)).thenReturn(cardDTO);

        List<CardDTO> result = cardService.findAllCards();

        assertNotNull(result);

        verify(cardRepository, times(1)).findAll();
        verify(cardMapper, times(1)).makeACardDTO(card);

        assertEquals(123L, result.getFirst().getId());
    }
    //SaveCardTest
    @Test
    void saveCardTest() {
        User user = makeAUser();
        user.setId(1L);

        Card card = makeACard(user);
        card.setId(123L);

        CardDTO cardDTO = makeACardDTO(card);
        cardDTO.setId(card.getId());


        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.makeACardDTO(card)).thenReturn(cardDTO);
        when(cardMapper.makeACard(cardDTO)).thenReturn(card);

        CardDTO result = cardService.saveCard(cardDTO);

        assertNotNull(result);

        assertNotNull(result);
        assertEquals(card.getId(), result.getId());
        assertEquals(card.getBalance(), result.getBalance());

        verify(cardRepository, times(1)).save(any(Card.class));
        verify(cardMapper, times(1)).makeACardDTO(card);
    }
    @Test
    void saveCardTest_NegativeBalance() {
        User user = makeAUser();
        user.setId(1L);

        Card card = makeACard(user);
        card.setId(123L);

        CardDTO cardDTO = makeACardDTO(card);
        cardDTO.setId(card.getId());
        cardDTO.setBalance(-100L);


        NegativeBalanceException exception = assertThrows(
                NegativeBalanceException.class,
                () -> cardService.saveCard(cardDTO)
        );

        assertEquals("Недостаточно средств", exception.getMessage());

        verify(cardRepository, never()).save(any(Card.class));
        verify(cardMapper, never()).makeACard(any(CardDTO.class));
        verify(cardMapper, never()).makeACardDTO(any(Card.class));
    }
    //blockCardTest
    @Test
    void blockCardTest() {
        User user = makeAUser();
        user.setId(1L);

        Card card = makeACard(user);
        card.setId(1L);
        card.setStatus(Status.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(
                invocation -> invocation.<Card>getArgument(0));

        when(cardMapper.makeACardDTO(any(Card.class))).thenAnswer(invocation -> {
            Card cardArg = invocation.getArgument(0);
            CardDTO dto = new CardDTO();
            dto.setId(cardArg.getId());
            dto.setStatus(cardArg.getStatus().name());
            return dto;
        });

        CardDTO result = cardService.blockCard(1L);

        assertEquals(Status.BLOCKED, card.getStatus());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("BLOCKED", result.getStatus());

        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(card);
        verify(cardMapper, times(1)).makeACardDTO(card);
    }
    //blockCardTest
    @Test
    void activateCardTest() {
        User user = makeAUser();
        user.setId(1L);

        Card card = makeACard(user);
        card.setId(1L);
        card.setStatus(Status.BLOCKED);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(
                invocation -> invocation.<Card>getArgument(0));

        when(cardMapper.makeACardDTO(any(Card.class))).thenAnswer(invocation -> {
            Card cardArg = invocation.getArgument(0);
            CardDTO dto = new CardDTO();
            dto.setId(cardArg.getId());
            dto.setStatus(cardArg.getStatus().name());
            return dto;
        });

        CardDTO result = cardService.activateCard(1L);

        assertEquals(Status.ACTIVE, card.getStatus());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ACTIVE", result.getStatus());

        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(card);
        verify(cardMapper, times(1)).makeACardDTO(card);
    }

    //deleteCard
    @Test
    void deleteCardTest() {
        User user = makeAUser();
        user.setId(1L);

        Card card = makeACard(user);
        card.setId(1L);

        doNothing().when(cardRepository).deleteById(1L);

        cardService.deleteCard(1L);

        verify(cardRepository, times(1)).deleteById(1L);
    }

    //тест метода transfer
    @Test
    void transfer_Success() {
        User user = makeAUser();

        Card fromCard = makeACard(user);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(1000L);
        toCard.setStatus(Status.ACTIVE);
        toCard.setUser(user);

        TransactionDTO transactionDTO = new TransactionDTO(1L, 2L, 1000L);
        UserDetailsImpl userDetails = makeUserDetails(user);

        when(cardRepository.getReferenceById(2L)).thenReturn(toCard);
        when(cardRepository.getReferenceById(1L)).thenReturn(fromCard);
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        cardService.transfer(transactionDTO, userDetails);

        verify(cardRepository, times(2)).getReferenceById(anyLong());
        verify(cardRepository, times(2)).save(any(Card.class));

        assertEquals(9000L, fromCard.getBalance());
        assertEquals(2000L, toCard.getBalance());
    }

    @Test
    void transfer_WhenFromCardNotOwnedByUser_ShouldThrowException() {
        User owner = makeAUser();
        owner.setId(1L);

        User attacker = makeAUser();
        attacker.setId(999L);

        Card fromCard = new Card();
        fromCard.setId(2L);
        fromCard.setUser(owner);

        Card toCard = new Card();
        toCard.setId(1L);
        toCard.setUser(owner);

        TransactionDTO transactionDTO = new TransactionDTO(2L, 1L, 1000L);
        UserDetailsImpl attackerDetails = makeUserDetails(attacker);

        when(cardRepository.getReferenceById(2L)).thenReturn(fromCard);
        when(cardRepository.getReferenceById(1L)).thenReturn(toCard);

        assertThrows(DifferentIdentifierException.class, () -> {
            cardService.transfer(transactionDTO, attackerDetails);
        });

        verify(cardRepository, never()).save(any());
    }

    @Test
    void transfer_WhenToCardNotOwnedByUser_ShouldThrowException() {
        // Given
        User owner = makeAUser();
        owner.setId(1L);

        User attacker = makeAUser();
        attacker.setId(999L);

        Card fromCard = new Card();
        fromCard.setId(2L);
        fromCard.setUser(attacker);

        Card toCard = new Card();
        toCard.setId(1L);
        toCard.setUser(owner);

        TransactionDTO transactionDTO = new TransactionDTO(2L, 1L, 1000L);
        UserDetailsImpl attackerDetails = makeUserDetails(attacker);

        when(cardRepository.getReferenceById(2L)).thenReturn(fromCard);
        when(cardRepository.getReferenceById(1L)).thenReturn(toCard);

        assertThrows(DifferentIdentifierException.class, () -> {
            cardService.transfer(transactionDTO, attackerDetails);
        });

        verify(cardRepository, never()).save(any());
    }

    @Test
    void transfer_WhenFromCardIsNotActive_ShouldThrowException() {
        User user = makeAUser();

        Card fromCard = new Card();
        fromCard.setId(2L);
        fromCard.setUser(user);
        fromCard.setStatus(Status.BLOCKED);

        Card toCard = new Card();
        toCard.setId(1L);
        toCard.setUser(user);
        toCard.setStatus(Status.ACTIVE);

        TransactionDTO transactionDTO = new TransactionDTO(2L, 1L, 1000L);
        UserDetailsImpl userDetails = makeUserDetails(user);

        when(cardRepository.getReferenceById(2L)).thenReturn(fromCard);
        when(cardRepository.getReferenceById(1L)).thenReturn(toCard);

        assertThrows(UnactiveCardException.class, () -> {
            cardService.transfer(transactionDTO, userDetails);
        });

        verify(cardRepository, never()).save(any());
    }

    @Test
    void transfer_WhenToCardIsNotActive_ShouldThrowException() {
        User user = makeAUser();

        Card fromCard = new Card();
        fromCard.setId(2L);
        fromCard.setUser(user);
        fromCard.setStatus(Status.ACTIVE);

        Card toCard = new Card();
        toCard.setId(1L);
        toCard.setUser(user);
        toCard.setStatus(Status.BLOCKED);

        TransactionDTO transactionDTO = new TransactionDTO(2L, 1L, 1000L);
        UserDetailsImpl userDetails = makeUserDetails(user);

        when(cardRepository.getReferenceById(2L)).thenReturn(fromCard);
        when(cardRepository.getReferenceById(1L)).thenReturn(toCard);

        assertThrows(UnactiveCardException.class, () -> {
            cardService.transfer(transactionDTO, userDetails);
        });

        verify(cardRepository, never()).save(any());
    }

    @Test
    void transfer_WhenCardsAreTheSame_ShouldThrowException() {
        User user = makeAUser();

        Card card = new Card();
        card.setId(1L);
        card.setUser(user);
        card.setStatus(Status.ACTIVE);

        TransactionDTO transactionDTO = new TransactionDTO(1L, 1L, 1000L);
        UserDetailsImpl userDetails = makeUserDetails(user);

        when(cardRepository.getReferenceById(1L)).thenReturn(card);

        assertThrows(SameCardException.class, () -> {
            cardService.transfer(transactionDTO, userDetails);
        });

        verify(cardRepository, never()).save(any());
    }
    //findByUserId
    @Test
    void getCardsByUserId_Test() {
        Long userId = 1L;
        User user = makeAUser();
        user.setId(userId);

        Card card1 = makeACard(user);
        Card card2 = makeACard(user);
        List<Card> expectedCards = Arrays.asList(card1, card2);

        CardDTO cardDTO1 = new CardDTO();
        cardDTO1.setId(card1.getId());

        CardDTO cardDTO2 = new CardDTO();
        cardDTO2.setId(card2.getId());

        UserDetailsImpl userDetails = makeUserDetails(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(cardRepository.findAllByUserId(userId)).thenReturn(expectedCards);
        when(cardMapper.makeACardDTO(card1)).thenReturn(cardDTO1);
        when(cardMapper.makeACardDTO(card2)).thenReturn(cardDTO2);

        List<CardDTO> result = cardService.findByUserId(userId, userDetails);

        assertNotNull(result);
        assertEquals(expectedCards.size(), result.size());

        verify(cardRepository, times(1)).findAllByUserId(userId);
        verify(cardMapper, times(1)).makeACardDTO(card1);
        verify(cardMapper, times(1)).makeACardDTO(card2);

        assertEquals(card1.getId(), result.getFirst().getId());
    }

    @Test
    void getCardsByUserId_WhenNoCards_ShouldReturnEmptyList() {
        Long userId = 1L;
        User user = makeAUser();
        user.setId(userId);

        UserDetailsImpl userDetails = makeUserDetails(user);

        when(cardRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<CardDTO> result = cardService.findByUserId(userId, userDetails);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(cardRepository, times(1)).findAllByUserId(userId);
        verify(cardMapper, never()).makeACardDTO(any());
    }
    //findById
    @Test
    void getCardsById_Test() {
        User user = makeAUser();
        user.setId(1L);

        Card card = makeACard(user);
        card.setId(123L);

        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(card.getId());

        when(cardRepository.findById(123L)).thenReturn(Optional.of(card));
        when(cardMapper.makeACardDTO(card)).thenReturn(cardDTO);

        CardDTO result = cardService.findCardById(123L);

        assertNotNull(result);

        verify(cardRepository, times(1)).findById(123L);
        verify(cardMapper, times(1)).makeACardDTO(card);

        assertEquals(123L, result.getId());
    }

    @Test
    void getCardsByIdNotFound_Test() {
        when(cardRepository.findById(333L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            cardService.findCardById(333L);});

        verify(cardMapper, never()).makeACardDTO(any());
    }
    //Тест метода Search
    @Test
    void search_WhenAllParamsNull_ShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(EntityNotFoundException.class, () -> {
            cardService.search(null, null, null, pageable);
        });

        verify(cardRepository, never()).findAll(isA(Specification.class), isA(Pageable.class));
    }

    @ParameterizedTest
    @MethodSource("searchParametersProvider")
    void search_WithDifferentParameters_ShouldCallRepository(
            Long userId, String status, LocalDate finalDate) {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> mockPage = new PageImpl<>(List.of());

        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

        cardService.search(userId, status, finalDate, pageable);

        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void search_ShouldMapResultsToDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Card card = makeACard(makeAUser());
        Page<Card> mockPage = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);
        when(cardMapper.makeACardDTO(card)).thenReturn(new CardDTO());

        Page<CardDTO> result = cardService.search(1L, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(cardMapper).makeACardDTO(card);
    }
    //Приватные методы теста
    private static Stream<Arguments> searchParametersProvider() {
        return Stream.of(
                Arguments.of(1L, null, null),
                Arguments.of(null, "ACTIVE", null),
                Arguments.of(null, null, LocalDate.now()),
                Arguments.of(1L, "ACTIVE", null),
                Arguments.of(1L, null, LocalDate.now()),
                Arguments.of(null, "ACTIVE", LocalDate.now()),
                Arguments.of(1L, "ACTIVE", LocalDate.now())
        );
    }

    private User makeAUser() {
        User user = new User();
        user.setId(1L);
        user.setPhoneNumber("+79540012325");
        user.setEmail("hellothere@gmail.com");
        user.setFirstName("Павел");
        user.setMiddleName("Павлов");
        user.setSecondName("Павлович");
        user.setPassword("securepassword113");
        HashSet<Role> roles = new HashSet<>();
        roles.add(new Role(1L, RoleEnum.ROLE_ADMIN));
        user.setRole(roles);

        return user;
    }

    private Card makeACard(User user) {

        Card card = new Card();
        card.setId(1L);
        card.setStatus(Status.ACTIVE);
        card.setUser(user);
        card.setBalance(10000L);
        card.setFinalDate(LocalDate.parse("2025-12-31"));

        return card;
    }

    private UserDetailsImpl makeUserDetails(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getFirstName() + user.getMiddleName() + user.getSecondName(),
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                user.getPhoneNumber()
        );
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