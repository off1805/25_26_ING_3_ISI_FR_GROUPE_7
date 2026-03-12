package com.projetTransversalIsi.authentification.application.service.token;


import com.projetTransversalIsi.user.domain.User;
import io.jsonwebtoken.Claims;


public interface JwtService {
     String generateJwtToken(User user);
     Claims getClaimsFromJwt(String token);
}
