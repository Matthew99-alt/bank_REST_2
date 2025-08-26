package com.example.bankcards.configuration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface PostgreSQLContainerInitializer {

    PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.10").withReuse(true);

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }
}
