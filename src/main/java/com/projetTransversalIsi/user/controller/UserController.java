package com.projetTransversalIsi.user.controller;

import com.projetTransversalIsi.Filiere.application.dto.FiliereResponseDTO;
import com.projetTransversalIsi.Filiere.application.dto.SearchFiliereRequestDTO;
import com.projetTransversalIsi.security.domain.Permission;
import com.projetTransversalIsi.user.dto.*;
import com.projetTransversalIsi.user.services.interfaces.*;
import com.projetTransversalIsi.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final CreateUserUC createUserUC;
    private final SearchUserUC searchUserUC;
    private final RetrievePermissionsForUserByIdUC retrievePermissionsForUserByIdUC;
    private final DeleteUserUC deleteUserUC;
    private final FindUserByIdUC findUserByIdUC;
    private final ModifyUserStatusUC modifyUserStatusUC;

    @PostMapping("")
    public ResponseEntity<UserDetailsResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
        log.info("Requête de création d'utilisateur reçue pour l'email : {}", request.email());
        User user = createUserUC.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDetailsResponseDTO.fromDomain(user));
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<Set<PermissionResponseDTO>> getPermissionsForUser(@PathVariable("id") Long id) {
        Set<Permission> permissions = retrievePermissionsForUserByIdUC.execute(id);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(permissions.stream().map(PermissionResponseDTO::fromPermissionDomain)
                        .collect(Collectors.toSet()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<DeleteUserResponseDTO> deleteUser(@PathVariable("userId") Long userId) {
        log.info("Requête de suppression d'utilisateur reçue pour l'ID : {}", userId);
        DeleteUserRequestDTO command = new DeleteUserRequestDTO(userId);
        deleteUserUC.execute(command);
        User deletedUser = findUserByIdUC.execute(userId);
        return ResponseEntity.ok(DeleteUserResponseDTO.fromUser(deletedUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsResponseDTO> getUserById(@PathVariable("id") Long id) {
        log.info("Requête de récupération d'utilisateur pour l'ID : {}", id);

        User user = findUserByIdUC.execute(id);

        return ResponseEntity.ok(UserDetailsResponseDTO.fromDomain(user));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<UserDetailsResponseDTO> modifyStatus(@PathVariable("id") Long id,
            @RequestBody @Valid ModifyUserStatusDTO statusDTO) {
        log.info("Requête de modification de statut pour l'utilisateur ID : {} vers {}", id, statusDTO.status());
        User user = modifyUserStatusUC.execute(id, statusDTO);
        return ResponseEntity.ok(UserDetailsResponseDTO.fromDomain(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDetailsResponseDTO>> searchFilieres(
            @RequestParam(required = false) String userStatus,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        List<User> resultats = searchUserUC.execute(userStatus,email,role,includeDeleted);
        return ResponseEntity.ok(resultats.stream().map(UserDetailsResponseDTO::fromDomain).collect(Collectors.toList()));
    }

}