package com.example.bankcards.mapper;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.util.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Маппер, отдельный класс для выполнения операций по переводу сущности пользователя в DTO и наоборот
 */

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleRepository roleRepository;

    public User makeAUser(UserDTO userDTO) {
        User user = new User();

        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setMiddleName(userDTO.getMiddleName());
        user.setSecondName(userDTO.getSecondName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        // Конвертируем Set<String> в Set<Role>
        Set<Role> roles = new HashSet<>();
        if (userDTO.getRole() != null) {
            for (Role currentRole : userDTO.getRole()) {
                RoleEnum roleName = currentRole.getName();
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }
        user.setRole(roles);

        return user;
    }

    public UserDTO makeAUserDTO(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setMiddleName(user.getMiddleName());
        userDTO.setSecondName(user.getSecondName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setRole(user.getRole());

        return userDTO;
    }
}
