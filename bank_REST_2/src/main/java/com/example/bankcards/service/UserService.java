package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.DifferentIdentifierException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Класс сервис отвечающий за реализацию запросов контроллера и работу с объектами класса CardUser
 *
 * @see User
 * @see UsersRepository
 * @see UserDTO
 * @see UserMapper
 * @see com.example.bankcards.controller.UserController
 */

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::makeAUserDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Указанный пользователь не найден"));
        return userMapper.makeAUserDTO(user);
    }

    @Transactional(readOnly = true)
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Boolean existsByPhoneNumber(String email) {
        return userRepository.existsByPhoneNumber(email);
    }

    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        User savedUser = userRepository.save(userMapper.makeAUser(userDTO));
        return userMapper.makeAUserDTO(savedUser);
    }

    @Transactional
    public void deleteCard(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDTO editCard(UserDTO userDTO) {
        User user = userMapper.makeAUser(userDTO);
        userRepository.save(user);
        return userDTO;
    }
}
