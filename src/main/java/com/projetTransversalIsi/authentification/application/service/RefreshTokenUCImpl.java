package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;
import com.projetTransversalIsi.authentification.application.dto.RefreshRequestDTO;
import com.projetTransversalIsi.authentification.application.service.token.JwtService;
import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.authentification.domain.RefreshTokenRepository;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.services.FindUserByIdUC;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenUCImpl implements RefreshTokenUC {

    private final RegisterNewRefreshTokenUC registerNewRefreshToken;
    private final FindUserByIdUC findUserByIdUC;
    private final JwtService jwtService;
    private final RefreshTokenRepository rTokenRepo;

    @Override
    @Transactional
    public LoginResponseDTO execute(RefreshRequestDTO command) {

        RefreshToken rToken = rTokenRepo.getRefreshTokenById(command.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Token " + command.getRefreshToken() + " non present en bd."));

        if (!rToken.isValid()) {
            throw new RuntimeException("Refresh token invalide");
        }

        rToken.revoke();
        rTokenRepo.saveRefreshToken(rToken);

        User currentUser = findUserByIdUC.execute(rToken.getUserId());

        RefreshToken newRToken = registerNewRefreshToken.execute(currentUser);
        rTokenRepo.saveRefreshToken(newRToken);

        String token = jwtService.generateJwtToken(currentUser);

        String role = currentUser.getRole() != null ? currentUser.getRole().getName() : null;
        String displayName = currentUser.getEmail();

        return new LoginResponseDTO(
                token,
                newRToken.getToken(),
                role,
                displayName
        );
    }
}