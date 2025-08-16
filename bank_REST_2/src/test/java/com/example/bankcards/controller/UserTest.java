package com.example.bankcards.controller;

import com.example.bankcards.BankRestApplication;
import com.example.bankcards.configuration.NoSecurityTestConfig;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UsersRepository;
import com.example.bankcards.util.RoleEnum;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {BankRestApplication.class, NoSecurityTestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository UserRepository;

    @Autowired
    private CardRepository cardRepository;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        UserRepository.deleteAll();
    }

    @Test
    @Transactional
    public void testGetAllCardUsers() throws Exception {
        mockMvc.perform(
                        get("/user/all")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @Transactional
    public void testGetCardUserById() throws Exception {
        User user = makeACardUserForTests();

        mockMvc.perform(
                        get("/user/").param("id", user.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @Transactional
    public void testDeleteUser() throws Exception {

        User user = makeACardUserForTests();

        mockMvc.perform(
                        delete("/user/delete").param("id", user.getId().toString())
                )
                .andExpect(status().isOk());

        Assertions.assertFalse(cardRepository.findById(user.getId()).isPresent());
    }

    @Test
    @Transactional
    public void testEditUser() throws Exception {

        User user = makeACardUserForTests();

        String requestBody = "{\n" +
                "    \"id\":\"" + user.getId() + "\",\n" +
                "    \"firstName\": \"Иган\",\n" +
                "    \"secondName\": \"Иганов\",\n" +
                "    \"middleName\": \"Иванович\",\n" +
                "    \"email\": \"ivanov21@example.com\",\n" +
                "    \"phoneNumber\": \"+79391233567\"\n" +
                "}";

        mockMvc.perform(
                        put("/user/edit").contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
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

        return UserRepository.save(user);
    }
}
