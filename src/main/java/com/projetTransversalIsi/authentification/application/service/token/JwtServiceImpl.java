package com.projetTransversalIsi.authentification.application.service.token;


import com.projetTransversalIsi.authentification.application.exceptions.InvalidTokenException;
import com.projetTransversalIsi.authentification.application.exceptions.KeyManagement;
import com.projetTransversalIsi.user.domain.User;
import io.jsonwebtoken.Claims;
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
                .claim("email",user.getEmail())
                .claim("role",user.getRole().getName())
                .claim("permissions",user.getIdPermissions())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ jwtExpirationMs))
                .signWith(keyManagement.loadPrivateKey())
                .compact();
    }

    @Override
    public Claims getClaimsFromJwt(String token){
        try{
            return
                    Jwts.parser()
                            .verifyWith(keyManagement.loadPublicKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();



        }catch (ExpiredJwtException e){
            throw new InvalidTokenException("Token expire: "+e.getMessage());
        }catch (JwtException | IllegalArgumentException e){
            throw new InvalidTokenException("Token invalide: "+ e.getMessage());
        }
    }
}
