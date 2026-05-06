package com.projetTransversalIsi.justificatif.web;

import com.projetTransversalIsi.justificatif.application.dto.DecisionJustificatifDTO;
import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;
import com.projetTransversalIsi.justificatif.application.dto.SoumettreJustificatifDTO;
import com.projetTransversalIsi.justificatif.application.use_cases.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<JustificatifResponseDTO> soumettre(
            @RequestParam Long etudiantId,
            @RequestParam(required = false) Long seanceId,
            @RequestParam String motif,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateAbsence,
            @RequestParam(required = false) MultipartFile fichier) {
        SoumettreJustificatifDTO dto = new SoumettreJustificatifDTO(etudiantId, seanceId, motif, dateAbsence);
        return ResponseEntity.status(HttpStatus.CREATED).body(soumettreUC.execute(dto, fichier));
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
}
