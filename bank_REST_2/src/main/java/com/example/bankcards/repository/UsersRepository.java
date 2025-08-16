package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий отвечающий за работу с таблицей указанной в сущности CardUser
 * @see  User
*/
@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phone);
    Optional<User> findByEmail(String email);
}