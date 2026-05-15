package com.projetTransversalIsi.justificatif.web;

import com.projetTransversalIsi.justificatif.application.dto.DecisionJustificatifDTO;
import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;
import com.projetTransversalIsi.justificatif.application.dto.SoumettreJustificatifDTO;
import com.projetTransversalIsi.justificatif.application.use_cases.*;
import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.infrastructure.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/justificatifs")
@RequiredArgsConstructor
public class JustificatifController {

    private final SoumettreJustificatifUC soumettreUC;
    private final ApprouverJustificatifUC approuverUC;
    private final RejeterJustificatifUC rejeterUC;
    private final ListerJustificatifsUC listerUC;
    private final SpringDataUserRepository userRepository;

    // etudiantId résolu depuis le principal — non exposé au client (M6).
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<JustificatifResponseDTO> soumettre(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) List<Long> seanceIds,
            @RequestParam String motif,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateAbsence,
            @RequestParam(required = false) List<MultipartFile> fichiers) {

        Long etudiantId = resolveEtudiantId(principal);
        SoumettreJustificatifDTO dto = new SoumettreJustificatifDTO(
                etudiantId, seanceIds, motif, message, dateAbsence);
        return ResponseEntity.status(HttpStatus.CREATED).body(soumettreUC.execute(dto, fichiers));
    }

    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<JustificatifResponseDTO>> getByEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(listerUC.getByEtudiant(etudiantId));
    }

    @GetMapping
    public ResponseEntity<List<JustificatifResponseDTO>> getAll() {
        return ResponseEntity.ok(listerUC.getAll());
    }

    @PatchMapping("/approuver")
    public ResponseEntity<JustificatifResponseDTO> approuver(@RequestBody DecisionJustificatifDTO dto) {
        return ResponseEntity.ok(approuverUC.execute(dto));
    }

    @PatchMapping("/rejeter")
    public ResponseEntity<JustificatifResponseDTO> rejeter(@RequestBody DecisionJustificatifDTO dto) {
        return ResponseEntity.ok(rejeterUC.execute(dto));
    }

    private Long resolveEtudiantId(UserPrincipal principal) {
        if (principal == null) {
            throw new IllegalStateException("Authentification requise");
        }
        return userRepository.findById(principal.userId())
                .map(u -> u.getProfile().getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
    }
}
