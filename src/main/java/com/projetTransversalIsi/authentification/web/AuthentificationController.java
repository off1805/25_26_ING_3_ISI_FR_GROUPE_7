package com.projetTransversalIsi.authentification.web;


import com.projetTransversalIsi.authentification.application.service.use_case.LoginUC;
import com.projetTransversalIsi.authentification.application.dto.LoginRequestDTO;
import com.projetTransversalIsi.authentification.application.dto.LoginResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthentificationController {
    private final LoginUC loginUc;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request){
        LoginResponseDTO tokenAndRToken= loginUc.execute(request);
        return ResponseEntity.ok(tokenAndRToken);
    }

}
