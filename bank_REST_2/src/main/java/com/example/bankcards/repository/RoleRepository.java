package com.example.bankcards.repository;

import com.example.bankcards.entity.Role;
import com.example.bankcards.util.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);

    List<Role> findByNameIn(Set<RoleEnum> rolesToFind);
}
