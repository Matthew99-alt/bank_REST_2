package com.example.bankcards.controller;

import com.example.bankcards.configuration.NoSecurityTestConfig;
import com.example.bankcards.configuration.PostgreSQLContainerInitializer;
import com.example.bankcards.dto.TransactionDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UsersRepository;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.UserDetailsImpl;
import com.example.bankcards.util.RoleEnum;
import com.example.bankcards.util.Status;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = NoSecurityTestConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CardControllerTest implements PostgreSQLContainerInitializer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UsersRepository cardUserRepository;

    @MockitoBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        cardUserRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testGetAllCards() throws Exception {
        mockMvc.perform(
                        get("/cards/")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @Transactional
    public void testGetAllCardsByUserId() throws Exception {
        Card card = makeACardForTests();

        UserDetailsImpl userDetails = makeUserDetails(card.getUser());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(
                        get("/cards/userId").param("id", card.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @Transactional
    public void testGetCardById() throws Exception {
        Card card = makeACardForTests();

        mockMvc.perform(
                        get("/cards/").param("id", card.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @Transactional
    public void testSaveCard() throws Exception {

        Card card = makeACardForTests();

        String requestBody = "{\n" +
                "    \"finalDate\": \"2025-11-11\",\n" +
                "    \"status\": \"ACTIVE\",\n" +
                "    \"balance\": 100000,\n" +
                "    \"userId\":" + card.getUser().getId() + "\n" +
                "}";

        mockMvc.perform(
                        post("/cards/").contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @Transactional
    void blockCard_ShouldReturnBlockedCard() throws Exception {

        Card card = makeACardForTests();

        mockMvc.perform(
                        patch("/cards/{id}/block", card.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        Card updatedCard = cardRepository.findById(card.getId()).orElseThrow();
        Assertions.assertEquals(Status.BLOCKED, updatedCard.getStatus());
    }

    @Test
    @Transactional
    void activeCard_ShouldReturnActive() throws Exception {

        Card card = makeACardForTests();

        card.setStatus(Status.BLOCKED);

        mockMvc.perform(
                        patch("/cards/{id}/activate", card.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        Card updatedCard = cardRepository.findById(card.getId()).orElseThrow();
        Assertions.assertEquals(Status.ACTIVE, updatedCard.getStatus());
    }


    @Test
    @Transactional
    public void testDeleteCard() throws Exception {

        Card card = makeACardForTests();

        UserDetailsImpl userDetails = makeUserDetails(card.getUser());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);


        mockMvc.perform(
                        delete("/cards/delete").param("id", card.getId().toString())
                )
                .andExpect(status().isOk());

        Assertions.assertFalse(cardRepository.findById(card.getId()).isPresent());
    }

    @Test
    @Transactional
    public void testTransferCard() throws Exception {

        TransactionDTO transactionDTO = makeATransactionDTOForTests();

        UserDetailsImpl userDetails = makeUserDetails(cardRepository.getReferenceById(
                cardUserRepository.getReferenceById(transactionDTO.toCardId()).getId()).getUser());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String requestBody = "{\n" +
                "    \"fromCardId\":" + transactionDTO.toCardId() + ",\n" +
                "    \"toCardId\": " + transactionDTO.fromCardId() + ",\n" +
                "    \"amount\": 1000\n" +
                "}";

        mockMvc.perform(
                        post("/cards/transfer").contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    private Card makeACardForTests() {
        Card card = new Card();
        User user = makeACardUserForTests();


        card.setStatus(Status.ACTIVE);
        card.setUser(user);
        card.setBalance(10000L);
        card.setFinalDate(LocalDate.parse("2025-12-31"));

        return cardRepository.save(card);
    }

    private User makeACardUserForTests() {
        User user = new User();
        user.setPhoneNumber("+79540012325");
        user.setEmail("hellothere@gmail.com");
        user.setFirstName("Павел");
        user.setMiddleName("Павлов");
        user.setSecondName("Павлович");
        user.setPassword("securepassword113");
        HashSet<Role> roles = new HashSet<>();
        roles.add(new Role(1L, RoleEnum.ROLE_ADMIN));
        user.setRole(roles);

        return cardUserRepository.save(user);
    }

    private TransactionDTO makeATransactionDTOForTests() {

        Card card1 = makeACardForTests();
        Card card = new Card();
        card.setStatus(Status.ACTIVE);
        card.setUser(card1.getUser());
        card.setBalance(10000L);
        card.setFinalDate(LocalDate.parse("2025-12-31"));
        cardRepository.save(card);

        return new TransactionDTO(card.getId(), card1.getId(), 100000L);
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
}
