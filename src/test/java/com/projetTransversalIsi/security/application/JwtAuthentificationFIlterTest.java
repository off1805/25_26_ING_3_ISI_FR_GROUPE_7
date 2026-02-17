package com.projetTransversalIsi.security.application;

import com.projetTransversalIsi.authentification.application.exceptions.InvalidTokenException;
import com.projetTransversalIsi.authentification.application.service.token.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthentificationFIlterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setsAuthenticationFromToken() throws Exception {
        JwtService tokenService = mock(JwtService.class);
        ClaimsBuilder claims = Jwts.claims();

        claims.subject("42");

        claims.add("role", "ADMIN");
        claims.add("permissions", List.of("PERM_A", "PERM_B"));
        when(tokenService.getClaimsFromJwt("good")).thenReturn(claims.build());

        JwtAuthentificationFIlter filter = new JwtAuthentificationFIlter(tokenService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer good");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(42L, auth.getPrincipal());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("PERM_A")));
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("PERM_B")));
        verify(chain).doFilter(request, response);
    }

    @Test
    void clearsContextOnInvalidToken() throws Exception {
        JwtService tokenService = mock(JwtService.class);
        when(tokenService.getClaimsFromJwt("bad"))
                .thenThrow(new InvalidTokenException("bad"));

        JwtAuthentificationFIlter filter = new JwtAuthentificationFIlter(tokenService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer bad");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("user", null));

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}
