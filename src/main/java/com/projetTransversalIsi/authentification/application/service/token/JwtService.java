package com.projetTransversalIsi.authentification.application.service.token;


import com.projetTransversalIsi.user.domain.User;


public interface JwtService {
     String generateJwtToken(User user);
     Long getUserIdFromJwt(String token);
}
