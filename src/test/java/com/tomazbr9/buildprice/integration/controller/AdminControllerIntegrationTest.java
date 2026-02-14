package com.tomazbr9.buildprice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomazbr9.buildprice.dto.user.UserResponseDTO;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.integration.AbstractIntegrationTest;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.util.TestAuthHelper;
import com.tomazbr9.buildprice.util.TestUserFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class AdminControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String jwt;

    @BeforeEach
    void setup() throws Exception {

        var admin = TestUserFactory.createUser(
                "admin@email.com",
                "123456",
                userRepository,
                roleRepository,
                RoleName.ROLE_ADMIN,
                passwordEncoder
        );

        TestUserFactory.createDefaultUser(userRepository, roleRepository, passwordEncoder);

        jwt = TestAuthHelper.getJwtToken(
                mockMvc,
                objectMapper,
                admin.getEmail(),
                "123456"
        );
    }

    @Test
    void shouldGetUsers() throws Exception {

        var result = mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        List<UserResponseDTO> users = List.of(objectMapper.readValue(result.getResponse().getContentAsString(), UserResponseDTO[].class));

        assertThat(users).isNotEmpty();
        assertThat(users.get(0).email()).isEqualTo("user@email.com");
    }

}

