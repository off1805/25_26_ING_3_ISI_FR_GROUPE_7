package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.profil.infrastructure.JpaAPProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataAPProfileRepository;
import com.projetTransversalIsi.web_application.ap.dto.AbsenceEtudiantDTO;
import com.projetTransversalIsi.web_application.ap.dto.UeDTO;
import com.projetTransversalIsi.web_application.ap.repository.SpringDataAPAbsencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ap")
@RequiredArgsConstructor
public class APAbsencesController {

    private final SpringDataAPAbsencesRepository absencesRepo;
    private final SpringDataAPProfileRepository apProfileRepo;

    @GetMapping("/absences")
    public ResponseEntity<List<AbsenceEtudiantDTO>> getAbsences(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Long ueId,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(List.of());

        LocalDate debut = dateDebut != null && !dateDebut.isBlank() ? LocalDate.parse(dateDebut) : null;
        LocalDate fin   = dateFin   != null && !dateFin.isBlank()   ? LocalDate.parse(dateFin)   : null;

        List<AbsenceEtudiantDTO> result = absencesRepo
                .findAbsencesParEtudiant(filiereId, classeId, ueId, debut, fin)
                .stream()
                .map(r -> new AbsenceEtudiantDTO(
                        r.getEtudiantId(),
                        r.getNom(),
                        r.getPrenom(),
                        r.getMatricule(),
                        r.getPhotoUrl(),
                        r.getClasseId(),
                        r.getClasseCode(),
                        r.getTotalEnregistrements(),
                        r.getTotalAbsences(),
                        r.getNbJustifiees(),
                        r.getNbEnAttente()))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/absences/ues")
    public ResponseEntity<List<UeDTO>> getUes(
            @AuthenticationPrincipal UserPrincipal principal) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(List.of());

        List<UeDTO> result = absencesRepo.findUesWithPresenceData(filiereId)
                .stream()
                .map(r -> new UeDTO(r.getId(), r.getLibelle(), r.getCode()))
                .toList();

        return ResponseEntity.ok(result);
    }

    private Long resolveApFiliereId(UserPrincipal principal) {
        if (principal == null) return null;
        return apProfileRepo.findByUserId(principal.userId())
                .map(JpaAPProfileEntity::getFiliereId)
                .orElse(null);
    }
}
