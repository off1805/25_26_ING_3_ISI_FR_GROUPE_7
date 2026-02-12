package com.projetTransversalIsi.security.application.services;

public interface PasswordHasherAC {

    boolean matches(String rawPassword, String hashedPassword);
    String encode(String rawPassword);
}
