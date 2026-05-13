package com.projetTransversalIsi.emploi_temps.web;

import com.projetTransversalIsi.emploi_temps.application.dto.*;
import com.projetTransversalIsi.emploi_temps.application.use_cases.*;
import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.infrastructure.JpaUserEntity;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presences")
@RequiredArgsConstructor
public class PresenceController {

    private final CreatePresenceListUC createPresenceListUC;
    private final GetPresenceListUC getPresenceListUC;
    private final DeletePresenceListUC deletePresenceListUC;
    private final AddPresenceRowUC addPresenceRowUC;
    private final UpdatePresenceRowUC updatePresenceRowUC;
    private final MarkStudentPresentUC markStudentPresentUC;
    private final SpringDataUserRepository userRepository;

    // GET /api/presences/scan?code=<uuid> — point d'entrée du scan QR côté étudiant.
    // L'étudiant doit être authentifié (JWT) ; son profileId est résolu depuis le token Spring Security.
    // idCode=null car le scan QR identifie le code par sa valeur (codeValeur), pas par son id.
    @GetMapping("/scan")
    public ResponseEntity<PresenceRowResponseDTO> scanQR(
            @RequestParam String code,
            @AuthenticationPrincipal UserPrincipal principal) {

        JpaUserEntity user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        // Résolution de l'étudiant : userId (compte) → profile.id (identité métier de l'étudiant).
        Long etudiantId = user.getProfile().getId();

        return ResponseEntity.ok(markStudentPresentUC.execute(
                new MarkStudentPresentCommand(etudiantId, null, code, null)
        ));
    }

    // POST /api/presences — l'enseignant ouvre une feuille de présence pour une séance.
    @PostMapping
    public ResponseEntity<PresenceListResponseDTO> create(@RequestBody CreatePresenceListDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createPresenceListUC.execute(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresenceListResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getPresenceListUC.getById(id));
    }

    // GET /api/presences/{id}/rows — retourne toutes les lignes (étudiants) d'une feuille.
    @GetMapping("/{id}/rows")
    public ResponseEntity<List<PresenceRowResponseDTO>> getRows(@PathVariable Long id) {
        return ResponseEntity.ok(getPresenceListUC.getRows(id));
    }

    // GET /api/presences/{id}/info-rows — retourne toutes les InfoPresenceRow de la feuille.
    // Utilisé par le frontend pour afficher le statut par-appel (isPresent null/true/false).
    @GetMapping("/{id}/info-rows")
    public ResponseEntity<List<InfoPresenceRowResponseDTO>> getInfoRows(@PathVariable Long id) {
        return ResponseEntity.ok(getPresenceListUC.getInfoRows(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deletePresenceListUC.execute(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/presences/rows — ajout manuel d'un étudiant à une feuille (par l'enseignant).
    @PostMapping("/rows")
    public ResponseEntity<PresenceRowResponseDTO> addRow(@RequestBody CreatePresenceRowDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addPresenceRowUC.execute(dto));
    }

    // PUT /api/presences/rows — correction manuelle de la présence/heures d'absence par l'enseignant.
    @PutMapping("/rows")
    public ResponseEntity<PresenceRowResponseDTO> updateRow(@RequestBody UpdatePresenceRowDTO dto) {
        return ResponseEntity.ok(updatePresenceRowUC.execute(dto));
    }
}
