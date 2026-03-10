package com.projetTransversalIsi.authentification.application.service;

import com.projetTransversalIsi.authentification.application.dto.LoginRequestDTO;
import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;
import com.projetTransversalIsi.authentification.application.service.token.JwtService;
import com.projetTransversalIsi.authentification.domain.RefreshToken;
import com.projetTransversalIsi.security.services.PasswordHasherAC;
import com.projetTransversalIsi.user.services.interfaces.GetPasswordByEmail;
import com.projetTransversalIsi.user.services.interfaces.GetUserByEmail;
import com.projetTransversalIsi.user.domain.User;
import com.projetTransversalIsi.user.domain.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class LoginUCImpl implements LoginUC {

    private final GetPasswordByEmail getPasswordByEmail;
    private final PasswordHasherAC passwordHasher;
    private final JwtService jwtS;
    private final GetUserByEmail getUserByEmail;
    private final RegisterNewRefreshTokenUC registerNewRefreshToken;

    @Override
    public LoginResponseDTO execute(LoginRequestDTO command){
        String truePassword= getPasswordByEmail.getPasswordByEmail(command.getEmail())
                .orElseThrow(()->new UserNotFoundException(command.getEmail()));
        if(!passwordHasher.matches(command.getPassword(),truePassword)){
            throw new RuntimeException("Mot de passe ou email incorrect.");
        }
        User user= getUserByEmail.getByEmail(
                command.getEmail()).orElseThrow(
                ()->new UserNotFoundException(command.getEmail())
        );
        String token= jwtS.generateJwtToken(user);

        RefreshToken rToken= registerNewRefreshToken.execute(user);
        return new LoginResponseDTO(token,rToken.getToken());
    }

}
