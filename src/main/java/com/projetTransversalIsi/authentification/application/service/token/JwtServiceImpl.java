package com.projetTransversalIsi.authentification.application.service.token;


import com.projetTransversalIsi.authentification.application.exceptions.InvalidTokenException;
import com.projetTransversalIsi.authentification.application.exceptions.KeyManagement;
import com.projetTransversalIsi.user.domain.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtServiceImpl implements  JwtService{

    private final KeyManagement keyManagement;

    @Value("${security.jwt.expiration-ms}")
    private Long jwtExpirationMs;

    @Override
    public String generateJwtToken(User user){
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("userEmail",user.getEmail())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ jwtExpirationMs))
                .signWith(keyManagement.loadPrivateKey())
                .compact();
    }

    @Override
    public Long getUserIdFromJwt(String token){
        try{
            return Long.valueOf(
                    Jwts.parser()
                            .verifyWith(keyManagement.loadPublicKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .getSubject()
            );

        }catch (ExpiredJwtException e){
            throw new InvalidTokenException("Token expire: "+e.getMessage());
        }catch (JwtException | IllegalArgumentException e){
            throw new InvalidTokenException("Token invalide: "+ e.getMessage());
        }
    }
}
