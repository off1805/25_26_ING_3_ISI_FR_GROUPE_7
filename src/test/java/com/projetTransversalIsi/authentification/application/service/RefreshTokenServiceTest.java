package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.authentification.domain.RefreshTokenRepository;
import com.projetTransversalIsi.user.domain.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenServiceTest {

    @Test
    void registerNewRTokenForUserSetsFields() {
        RefreshTokenRepository repo = mock(RefreshTokenRepository.class);
        when(repo.saveRefreshToken(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenService service = new RefreshTokenService(repo);
        User user = new User();
        user.setId(10L);

        RefreshToken token = service.registerNewRTokenForUser(user);

        assertEquals(10L, token.getUserId());
        assertNotNull(token.getToken());
        assertNotNull(token.getCreatedAt());
        assertNotNull(token.getExpiresAt());
        assertTrue(token.getExpiresAt().after(token.getCreatedAt()));
    }

    @Test
    void findRefreshTokenByIdDelegatesToRepository() {
        RefreshTokenRepository repo = mock(RefreshTokenRepository.class);
        RefreshToken expected = new RefreshToken();
        when(repo.getRefreshTokenById("id-1")).thenReturn(Optional.of(expected));

        RefreshTokenService service = new RefreshTokenService(repo);

        Optional<RefreshToken> result = service.findRefreshTokenById("id-1");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(repo).getRefreshTokenById("id-1");
    }
}
