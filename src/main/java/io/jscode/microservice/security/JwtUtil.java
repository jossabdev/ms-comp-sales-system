package io.jscode.microservice.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration.days}")
    private long expirationDays;

    private Algorithm algorithm;

    public String create(String username){
        algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
                 .withSubject(username)
                 .withIssuer(issuer)
                 .withIssuedAt(Instant.now())
                 .withExpiresAt(Instant.now().plus(expirationDays, ChronoUnit.DAYS))
                 .sign(algorithm);
    }

    public boolean isValid(String jwt){
        algorithm = Algorithm.HMAC256(secretKey);
        try {
            JWT.require(algorithm)
               .build()
               .verify(jwt);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public String getUsername(String jwt){
        return JWT.require(algorithm)
               .build()
               .verify(jwt)
               .getSubject();
    }
}
