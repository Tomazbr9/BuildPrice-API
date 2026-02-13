package com.tomazbr9.buildprice.uit.service;

import com.tomazbr9.buildprice.dto.auth.JwtTokenDTO;
import com.tomazbr9.buildprice.dto.auth.LoginDTO;
import com.tomazbr9.buildprice.dto.user.UserRequestDTO;
import com.tomazbr9.buildprice.entity.Role;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.exception.RoleNotFoundException;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.security.SecurityConfiguration;
import com.tomazbr9.buildprice.security.jwt.JwtTokenService;
import com.tomazbr9.buildprice.security.model.UserDetailsImpl;
import com.tomazbr9.buildprice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private SecurityConfiguration securityConfiguration;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {

        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        UserRequestDTO request = new UserRequestDTO("teste@email.com", "123456");

        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.of(role));

        when(securityConfiguration.passwordEncoder())
                .thenReturn(new BCryptPasswordEncoder());

        authService.registerUser(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRoleNotFound() {

        UserRequestDTO request = new UserRequestDTO("teste@email.com", "123456");

        when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class,
                () -> authService.registerUser(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldAuthenticateAndGenerateToken() {

        LoginDTO request = new LoginDTO("teste@email.com", "123456");

        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtTokenService.generateToken(userDetails))
                .thenReturn("fake-jwt-token");

        JwtTokenDTO response = authService.authenticateUser(request);

        assertEquals("fake-jwt-token", response.token());

        verify(authenticationManager).authenticate(any());
        verify(jwtTokenService).generateToken(userDetails);
    }



}
