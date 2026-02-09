package com.projetTransversalIsi.user.web;

import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.user.application.dto.CreateUserRequestDTO;
import com.projetTransversalIsi.user.application.dto.PermissionResponseDTO;
import com.projetTransversalIsi.user.application.dto.UserDetailsResponseDTO;
import com.projetTransversalIsi.user.application.use_cases.CreateUserUC;
import com.projetTransversalIsi.user.application.use_cases.RetrievePermissionsForUserByIdUC;
import com.projetTransversalIsi.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final CreateUserUC createUserUC;
    private final RetrievePermissionsForUserByIdUC retrievePermissionsForUserByIdUC;

    @PostMapping
    public ResponseEntity<UserDetailsResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request){
        log.info("Requête de création d'utilisateur reçue pour l'email : {}", request.email());
        User user = createUserUC.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDetailsResponseDTO.fromDomain(user));
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<Set<PermissionResponseDTO>> getPermissionsForUser(@PathVariable Long id){
        Set<Permission> permissions= retrievePermissionsForUserByIdUC.execute(id);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body( permissions.stream().map(PermissionResponseDTO::fromPermissionDomain).collect(Collectors.toSet())
        );


    }

}
