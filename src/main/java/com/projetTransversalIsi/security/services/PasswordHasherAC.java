package com.projetTransversalIsi.security.services;

public interface PasswordHasherAC {

    boolean matches(String rawPassword, String hashedPassword);
    String encode(String rawPassword);
}
