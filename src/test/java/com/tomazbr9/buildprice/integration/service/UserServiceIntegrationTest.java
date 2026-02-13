package com.tomazbr9.buildprice.integration.service;

import com.tomazbr9.buildprice.dto.user.UserPatchDTO;
import com.tomazbr9.buildprice.dto.user.UserResponseDTO;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.exception.UserNotFoundException;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User createUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return userRepository.save(user);
    }

    @Test
    void shouldReturnUserWhenExists() {

        createUser("bruno@email.com", "123");

        UserResponseDTO response = userService.getUser("bruno@email.com");

        assertNotNull(response);
        assertEquals("bruno@email.com", response.email());
    }

    @Test
    void shouldThrowExceptionWhenUserNotExists_getUser() {

        assertThrows(UserNotFoundException.class,
                () -> userService.getUser("notfound@email.com"));
    }

    @Test
    void shouldUpdateEmail() {

        createUser("old@email.com", "123");

        UserPatchDTO request = new UserPatchDTO("new@email.com", null);

        UserResponseDTO response =
                userService.putUser(request, "old@email.com");

        assertEquals("new@email.com", response.email());

        assertTrue(userRepository.findByEmail("new@email.com").isPresent());
    }

    @Test
    void shouldUpdatePassword() {

        createUser("user@email.com", "123");

        UserPatchDTO request = new UserPatchDTO(null, "456");

        userService.putUser(request, "user@email.com");

        User updated = userRepository.findByEmail("user@email.com").orElseThrow();

        assertEquals("456", updated.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenUserNotExists_putUser() {

        UserPatchDTO request = new UserPatchDTO("x@email.com", "123");

        assertThrows(UserNotFoundException.class,
                () -> userService.putUser(request, "no@email.com"));
    }

    @Test
    void shouldDeleteUser() {

        createUser("delete@email.com", "123");

        userService.deleteUser("delete@email.com");

        assertTrue(userRepository.findByEmail("delete@email.com").isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUserNotExists_deleteUser() {

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser("none@email.com"));
    }
}

