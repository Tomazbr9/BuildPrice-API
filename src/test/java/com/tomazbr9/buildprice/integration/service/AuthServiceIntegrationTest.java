package com.tomazbr9.buildprice.integration.service;

import com.tomazbr9.buildprice.dto.user.UserRequestDTO;
import com.tomazbr9.buildprice.entity.Role;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class AuthServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldRegisterUserSuccessfully() {

        // Garantir que ROLE_USER existe (idealmente via migration)
        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow();

        UserRequestDTO request =
                new UserRequestDTO("newuser@email.com", "123456");

        authService.registerUser(request);

        User savedUser = userRepository.findByEmail("newuser@email.com")
                .orElseThrow();

        assertNotNull(savedUser.getId());
        assertNotEquals("123456", savedUser.getPassword()); // deve estar criptografado
        assertTrue(savedUser.getRoles().contains(role));
    }

}

