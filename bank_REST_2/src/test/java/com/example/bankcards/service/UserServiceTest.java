package com.example.bankcards.service;

import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UsersRepository;
import com.example.bankcards.util.RoleEnum;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    //findAllCardsTest
    @Test
    void findAllUsersTest() {
        User user = makeAUser();
        user.setId(1L);

        UserDTO userDTO = makeAUserDTO(user);

        when(usersRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.makeAUserDTO(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.findAllUsers();

        assertNotNull(result);

        verify(usersRepository, times(1)).findAll();
        verify(userMapper, times(1)).makeAUserDTO(user);

        assertEquals(1L, result.getFirst().getId());
    }
    //findUserById
    @Test
    void findUserById() {
        User user = makeAUser();
        user.setId(1L);

        UserDTO userDTO = makeAUserDTO(user);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.makeAUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(usersRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).makeAUserDTO(user);

        assertEquals(1L, result.getId());
    }
    @Test
    void findUserByIdNotFound() {
        when(usersRepository.findById(333L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.findUserById(333L);});

        verify(userMapper, never()).makeAUserDTO(any());
    }
    //existByEmail
    @Test
    void existsByEmail_WhenEmailExists_ReturnsTrue() {
        String email = "test@example.com";

        when(usersRepository.existsByEmail(email)).thenReturn(true);

        Boolean result = userService.existsByEmail(email);

        assertTrue(result);
        verify(usersRepository, times(1)).existsByEmail(email);
    }
    @Test
    void existsByEmail_WhenEmailNotExists_ReturnsFalse() {
        String email = "notfound@example.com";

        when(usersRepository.existsByEmail(email)).thenReturn(false);

        Boolean result = userService.existsByEmail(email);

        assertFalse(result);
        verify(usersRepository, times(1)).existsByEmail(email);
    }
    //existByPhoneNumber
    @Test
    void existsByPhoneNumber_WhenEmailExists_ReturnsTrue() {
        String phoneNumber = "+790233415505";

        when(usersRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        Boolean result = userService.existsByPhoneNumber(phoneNumber);

        assertTrue(result);
        verify(usersRepository, times(1)).existsByPhoneNumber(phoneNumber);
    }
    @Test
    void existsByPhoneNumber_WhenEmailNotExists_ReturnsFalse() {
        String email = "notfound@example.com";

        when(usersRepository.existsByPhoneNumber(email)).thenReturn(false);

        Boolean result = userService.existsByPhoneNumber(email);

        assertFalse(result);
        verify(usersRepository, times(1)).existsByPhoneNumber(email);
    }
    //SaveUserTest
    @Test
    void saveUserTest() {
        User user = makeAUser();
        user.setId(1L);

        UserDTO userDTO = makeAUserDTO(user);
        userDTO.setId(user.getId());


        when(usersRepository.save(user)).thenReturn(user);
        when(userMapper.makeAUserDTO(user)).thenReturn(userDTO);
        when(userMapper.makeAUser(userDTO)).thenReturn(user);

        UserDTO result = userService.saveUser(userDTO);

        assertNotNull(result);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());

        verify(usersRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).makeAUserDTO(user);
    }

    //deleteUser
    @Test
    void deleteUserTest() {
        User user = makeAUser();
        user.setId(1L);

        doNothing().when(usersRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(usersRepository, times(1)).deleteById(1L);
    }
    //EditUserTest
    @Test
    void editUserTest() {
        User user = makeAUser();

        UserDTO userDTO = makeAUserDTO(user);

        when(userMapper.makeAUser(userDTO)).thenReturn(user);
        when(usersRepository.save(user)).thenReturn(user);

        UserDTO result = userService.editUser(userDTO);

        assertNotNull(result);
        assertEquals(userDTO, result);

        verify(userMapper, times(1)).makeAUser(userDTO);
        verify(usersRepository, times(1)).save(user);
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

    private UserDTO makeAUserDTO(User user) {
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