package com.projetTransversalIsi.authentification.application.service.use_case;

import com.projetTransversalIsi.authentification.application.dto.LoginRequestDTO;
import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;
import com.projetTransversalIsi.authentification.application.service.DefaultRefreshTokenService;
import com.projetTransversalIsi.authentification.application.service.LoginUCImpl;
import com.projetTransversalIsi.authentification.application.service.RegisterNewRefreshTokenUC;
import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.security.services.PasswordHasherAC;
import com.projetTransversalIsi.security.domain.Role;
import com.projetTransversalIsi.authentification.application.service.token.JwtService;
import com.projetTransversalIsi.user.application.services.DefaultUserService;
import com.projetTransversalIsi.user.application.services.GetPasswordByEmail;
import com.projetTransversalIsi.user.application.services.GetUserByEmail;
import com.projetTransversalIsi.user.domain.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginUCImplTest {

        @Test
        void executeReturnsTokensWhenCredentialsValid() {
                DefaultUserService defaultUserService = mock(DefaultUserService.class);
                GetPasswordByEmail getPasswordByEmail = mock(GetPasswordByEmail.class);
                PasswordHasherAC passwordHasher = mock(PasswordHasherAC.class);
                JwtService jwtService = mock(JwtService.class);
                GetUserByEmail getUserByEmail = mock(GetUserByEmail.class);
                RegisterNewRefreshTokenUC refreshTokenService = mock(RegisterNewRefreshTokenUC.class);

                LoginRequestDTO request = new LoginRequestDTO();
                request.setEmail("user@example.com");
                request.setPassword("secret123");

                when(getPasswordByEmail.getPasswordByEmail("user@example.com"))
                                .thenReturn(Optional.of("hashed"));
                when(passwordHasher.matches("secret123", "hashed")).thenReturn(true);

                Role role = new Role("ADMIN");
                User user = new User();
                user.setId(7L);
                user.setEmail("user@example.com");
                user.setRole(role);
                when(getUserByEmail.getByEmail("user@example.com")).thenReturn(Optional.of(user));

                when(jwtService.generateJwtToken(user)).thenReturn("jwt-token");
                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setToken("refresh-token");
                when(refreshTokenService.execute(user)).thenReturn(refreshToken);

                LoginUCImpl useCase = new LoginUCImpl(
                                getPasswordByEmail,
                                passwordHasher,
                                jwtService,
                                getUserByEmail,
                                refreshTokenService);

                LoginResponseDTO response = useCase.execute(request);

                assertEquals("jwt-token", response.getToken());
                assertEquals("refresh-token", response.getRefreshToken());
        }

        @Test
        void executeThrowsWhenPasswordInvalid() {
                DefaultUserService defaultUserService = mock(DefaultUserService.class);
                GetPasswordByEmail getPasswordByEmail = mock(GetPasswordByEmail.class);
                PasswordHasherAC passwordHasher = mock(PasswordHasherAC.class);
                JwtService jwtService = mock(JwtService.class);
                GetUserByEmail getUserByEmail = mock(GetUserByEmail.class);
                RegisterNewRefreshTokenUC refreshTokenService = mock(RegisterNewRefreshTokenUC.class);

                LoginRequestDTO request = new LoginRequestDTO();
                request.setEmail("user@example.com");
                request.setPassword("bad");

                when(getPasswordByEmail.getPasswordByEmail("user@example.com"))
                                .thenReturn(Optional.of("hashed"));
                when(passwordHasher.matches("bad", "hashed")).thenReturn(false);

                LoginUCImpl useCase = new LoginUCImpl(
                                getPasswordByEmail,
                                passwordHasher,
                                jwtService,
                                getUserByEmail,
                                refreshTokenService);

                assertThrows(RuntimeException.class, () -> useCase.execute(request));
                verify(getUserByEmail, never()).getByEmail("user@example.com");
        }
}
