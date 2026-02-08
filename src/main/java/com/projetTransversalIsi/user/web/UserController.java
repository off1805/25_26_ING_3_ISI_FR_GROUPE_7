package com.projetTransversalIsi.user.web;

import com.projetTransversalIsi.user.application.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.application.dto.UserDetailsResponseDTO;
import com.projetTransversalIsi.user.application.use_cases.CreateUserUC;
import com.projetTransversalIsi.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final CreateUserUC createUserUC;

    @PostMapping
    public ResponseEntity<UserDetailsResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request){
        log.info("Requête de création d'utilisateur reçue pour l'email : {}", request.email());
        User user = createUserUC.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDetailsResponseDTO.fromDomain(user));
    }

}
