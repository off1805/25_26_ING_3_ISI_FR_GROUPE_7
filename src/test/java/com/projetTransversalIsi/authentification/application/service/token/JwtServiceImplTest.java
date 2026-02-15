package com.projetTransversalIsi.authentification.application.service.token;

import com.projetTransversalIsi.authentification.application.exceptions.InvalidTokenException;
import com.projetTransversalIsi.authentification.application.exceptions.KeyManagement;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.user.domain.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceImplTest {

    @Test
    void generateAndParseToken() throws Exception {
        KeyPair keyPair = generateKeyPair();
        KeyManagement keyManagement = mock(KeyManagement.class);
        when(keyManagement.loadPrivateKey()).thenReturn(keyPair.getPrivate());
        when(keyManagement.loadPublicKey()).thenReturn(keyPair.getPublic());

        JwtServiceImpl service = new JwtServiceImpl(keyManagement);
        ReflectionTestUtils.setField(service, "jwtExpirationMs", 60000L);

        Role role = new Role("ADMIN");
        User user = new User();
        user.setId(42L);
        user.setEmail("user@example.com");
        user.setRole(role);
        user.setIdPermissions(Set.of("PERM_READ", "PERM_WRITE"));

        String token = service.generateJwtToken(user);
        assertNotNull(token);

        Claims claims = service.getClaimsFromJwt(token);
        assertEquals("42", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        List<String> permissions = claims.get("permissions", List.class);
        assertTrue(permissions.contains("PERM_READ"));
        assertTrue(permissions.contains("PERM_WRITE"));
    }

    @Test
    void invalidTokenThrows() throws Exception {
        KeyPair keyPair = generateKeyPair();
        KeyManagement keyManagement = mock(KeyManagement.class);
        when(keyManagement.loadPublicKey()).thenReturn(keyPair.getPublic());

        JwtServiceImpl service = new JwtServiceImpl(keyManagement);
        ReflectionTestUtils.setField(service, "jwtExpirationMs", 60000L);

        assertThrows(InvalidTokenException.class, () -> service.getClaimsFromJwt("bad.token"));
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
        return keyPairGenerator.generateKeyPair();
    }
}
