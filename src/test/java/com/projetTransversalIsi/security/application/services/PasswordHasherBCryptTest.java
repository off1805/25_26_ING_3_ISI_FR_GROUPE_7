package com.projetTransversalIsi.security.application.services;

import com.projetTransversalIsi.security.services.PasswordHasherBCrypt;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherBCryptTest {

    @Test
    void encodeAndMatchesRoundTrip() {
        PasswordHasherBCrypt hasher = new PasswordHasherBCrypt(new BCryptPasswordEncoder());

        String hash = hasher.encode("secret123");

        assertNotEquals("secret123", hash);
        assertTrue(hasher.matches("secret123", hash));
        assertFalse(hasher.matches("wrong", hash));
    }
}
