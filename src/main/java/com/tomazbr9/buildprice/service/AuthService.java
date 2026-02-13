package com.tomazbr9.buildprice.service;

import com.tomazbr9.buildprice.dto.auth.JwtTokenDTO;
import com.tomazbr9.buildprice.dto.auth.LoginDTO;
import com.tomazbr9.buildprice.dto.user.UserRequestDTO;
import com.tomazbr9.buildprice.entity.Role;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.enums.RoleName;
import com.tomazbr9.buildprice.exception.EmailAlreadyExistsException;
import com.tomazbr9.buildprice.exception.RoleNotFoundException;
import com.tomazbr9.buildprice.repository.RoleRepository;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.security.SecurityConfiguration;
import com.tomazbr9.buildprice.security.jwt.JwtTokenService;
import com.tomazbr9.buildprice.security.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfiguration securityConfiguration;

    public AuthService(JwtTokenService jwtTokenService, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, SecurityConfiguration securityConfiguration) {
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.securityConfiguration = securityConfiguration;
    }

    public void registerUser(UserRequestDTO request){

        Role roleDefault = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new RoleNotFoundException("Papel de usuário não encontrado"));

        if(userRepository.findByEmail(request.email()).isPresent()){
            throw new EmailAlreadyExistsException("Email ja existe.");
        }

        User user = new User(request.email(), securityConfiguration.passwordEncoder().encode(request.password()), Set.of(roleDefault));

        userRepository.save(user);
    }

    public JwtTokenDTO authenticateUser(LoginDTO request){

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new JwtTokenDTO(jwtTokenService.generateToken(userDetails));
    }

}
