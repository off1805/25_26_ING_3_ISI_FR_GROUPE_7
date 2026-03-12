package com.projetTransversalIsi.security.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordHasherBCrypt implements PasswordHasherAC {

    private final BCryptPasswordEncoder bCrypt;

    @Override
    public boolean matches(String rawPassword, String hashedPassword){
        return bCrypt.matches(rawPassword,hashedPassword);
    }

    @Override
    public String encode(String rawPassword){
        return bCrypt.encode(rawPassword);
    }
}
