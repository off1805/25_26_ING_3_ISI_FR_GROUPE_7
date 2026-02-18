package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.application.dto.RefreshRequestDTO;
import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.authentification.domain.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LogoutUCImpl implements LogoutUC {

    private final RefreshTokenRepository rTokenRepo;

    @Override
    @Transactional
    public void execute(RefreshRequestDTO command){
        RefreshToken rToken= rTokenRepo.getRefreshTokenById(command.getRefreshToken()).orElseThrow(
                ()->  new RuntimeException("Token non present en bd.")
        );
        rToken.revoke();
        rTokenRepo.saveRefreshToken(rToken);
    }

}
