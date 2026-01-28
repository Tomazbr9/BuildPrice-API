package com.tomazbr9.buildprice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tomazbr9.buildprice.security.model.UserDetailsImpl;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class JwtTokenService {

    // Chave secreta usada para assinar e validar o token (nunca expor isso publicamente)
    private static final String SECRET_KEY = "chave-secreta";

    // Identificador da aplicação que está emitindo o token
    private static final String ISSUER = "pizzurg-api";

    /**
     * Gera um token JWT com base nos dados do usuário.
     * O token inclui quem emitiu, quando foi criado, quando expira e o "dono" do token (usuário).
     */
    public String generateToken(UserDetailsImpl user) {
        try {
            // Algoritmo de segurança para assinar o token
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            // Cria e assina o token
            return JWT.create()
                    .withIssuer(ISSUER) // quem está emitindo o token
                    .withIssuedAt(creationDate()) // quando foi criado
                    .withExpiresAt(expirationDate()) // quando vai expirar
                    .withSubject(user.getUsername()) // quem é o usuário "dono" do token
                    .sign(algorithm); // assina o token com a chave secreta
        } catch (JWTCreationException exception){
            // Caso algo dê errado na criação do token
            throw new JWTCreationException("Erro ao gerar token.", exception);
        }
    }

    /**
     * Extrai o nome do usuário (subject) de dentro do token.
     * Serve para identificar quem está tentando acessar algo com esse token.
     */
    public String getSubjectFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            return JWT.require(algorithm)
                    .withIssuer(ISSUER) // garante que o token foi emitido pela nossa API
                    .build()
                    .verify(token) // verifica se o token é válido e não foi alterado
                    .getSubject(); // pega o "usuário" que está dentro do token
        } catch (JWTVerificationException exception){
            // Token inválido (modificado ou expirado)
            throw new JWTVerificationException("Token inválido ou expirado.");
        }
    }

    // Retorna a data e hora atual (criação do token)
    private Instant creationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    }

    // Define que o token vai expirar em 4 horas
    private Instant expirationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusHours(4).toInstant();
    }
}
