package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;
import com.projetTransversalIsi.authentification.application.dto.RefreshRequestDTO;
import com.projetTransversalIsi.authentification.application.service.token.JwtService;
import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.authentification.domain.RefreshTokenRepository;
import com.projetTransversalIsi.user.application.use_cases.FindUserByIdUC;
import com.projetTransversalIsi.user.domain.User;
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
    public LoginResponseDTO execute(RefreshRequestDTO command){
        System.out.println();
        System.out.println("un");

       RefreshToken rToken= rTokenRepo.getRefreshTokenById(command.getRefreshToken()).orElseThrow(
                ()-> new RuntimeException("Token "+command.getRefreshToken()+" non present en bd.")
        );
       if(!rToken.isValid()){
           throw new RuntimeException("Refresh token invalide");
       }

       rToken.revoke();
        rTokenRepo.saveRefreshToken(rToken);
        System.out.println();
        System.out.println("un");

       User currentUser= findUserByIdUC.execute(rToken.getUserId());
       RefreshToken newRToken= registerNewRefreshToken.execute(currentUser);
        System.out.println();
        System.out.println("deux");
       rTokenRepo.saveRefreshToken(newRToken);

       String token= jwtService.generateJwtToken(currentUser);
        System.out.println();
        System.out.println("trois");
       return new LoginResponseDTO(token,newRToken.getToken());
    }

}
