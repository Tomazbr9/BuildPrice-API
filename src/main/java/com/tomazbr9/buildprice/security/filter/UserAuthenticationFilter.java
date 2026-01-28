package com.tomazbr9.buildprice.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tomazbr9.buildprice.entity.User;
import com.tomazbr9.buildprice.repository.UserRepository;
import com.tomazbr9.buildprice.security.SecurityConfiguration;
import com.tomazbr9.buildprice.security.jwt.JwtTokenService;
import com.tomazbr9.buildprice.security.model.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Verifica se o endpoint exige autenticação
        if (checkIfEndpointIsNotPublic(request)) {
            String token = recoveryToken(request);

            if (token != null) {
                try {
                    // Extrai o nome do usuário a partir do token
                    String subject = jwtTokenService.getSubjectFromToken(token);

                    // Busca o usuário no banco de dados
                    User user = userRepository.findByEmail(subject)
                            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + subject));

                    // Cria o objeto com dados do usuário para o Spring Security
                    UserDetailsImpl userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), user.getPassword(), user.getRoles(), true);

                    // Cria a autenticação baseada no usuário e suas permissões
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    // Define a autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (JWTVerificationException e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token inválido ou expirado.");
                    return;
                } catch (UsernameNotFoundException e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Usuário não encontrado.");
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token JWT ausente.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // Recupera o token do cabeçalho Authorization, removendo o prefixo "Bearer "
    private String recoveryToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    // Verifica se o endpoint está em uma lista de rotas públicas (sem autenticação)
    private boolean checkIfEndpointIsNotPublic(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        return !Arrays.asList(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED)
                .contains(requestURI);
    }
}
