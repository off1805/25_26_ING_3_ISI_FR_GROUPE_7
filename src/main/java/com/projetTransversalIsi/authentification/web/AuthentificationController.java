package com.projetTransversalIsi.authentification.web;


import com.projetTransversalIsi.authentification.application.dto.RefreshRequestDTO;
import com.projetTransversalIsi.authentification.application.service.LoginUC;
import com.projetTransversalIsi.authentification.application.dto.LoginRequestDTO;
import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;
import com.projetTransversalIsi.authentification.application.service.LogoutUC;
import com.projetTransversalIsi.authentification.application.service.RefreshTokenUC;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("")
@RestController
@RequiredArgsConstructor
public class AuthentificationController {

    private final LoginUC loginUc;
    private final RefreshTokenUC refreshTokenUC;
    private final LogoutUC logout;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request){
        LoginResponseDTO tokenAndRToken= loginUc.execute(request);
        return ResponseEntity.ok(tokenAndRToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO request){
        LoginResponseDTO tokenAndRToken= refreshTokenUC.execute(request);
        return ResponseEntity.ok(tokenAndRToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequestDTO request ){
        logout.execute(request);
        return ResponseEntity.noContent().build();
    }

}
