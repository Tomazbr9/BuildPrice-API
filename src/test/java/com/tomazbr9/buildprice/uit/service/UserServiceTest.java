package com.tomazbr9.buildprice.uit.service;

import com.tomazbr9.buildprice.dto.user.UserPatchDTO;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.exception.UserNotFoundException;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserWhenUserExists() {

        User user = new User();
        user.setEmail("teste@email.com");

        when(userRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(user));

        var response = userService.getUser("teste@email.com");

        assertEquals("teste@email.com", response.email());
        verify(userRepository).findByEmail("teste@email.com");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.getUser("naoexiste@email.com")
        );

        verify(userRepository).findByEmail("naoexiste@email.com");
    }

    @Test
    void shouldUpdateUserEmailAndPassword() {

        User user = new User();
        user.setEmail("old@email.com");
        user.setPassword("oldpass");

        UserPatchDTO patchDTO = new UserPatchDTO("new@email.com", "newpass");

        when(userRepository.findByEmail("old@email.com"))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.putUser(patchDTO, "old@email.com");

        assertEquals("new@email.com", response.email());
        assertEquals("newpass", user.getPassword());

        verify(userRepository).save(user);
    }

    @Test
    void shouldDeleteUserWhenExists() {

        User user = new User();
        user.setEmail("delete@email.com");

        when(userRepository.findByEmail("delete@email.com"))
                .thenReturn(Optional.of(user));

        userService.deleteUser("delete@email.com");

        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {

        when(userRepository.findByEmail("notfound@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser("notfound@email.com")
        );

        verify(userRepository, never()).delete(any());
    }
}

