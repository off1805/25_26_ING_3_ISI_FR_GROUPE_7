package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.user.profil.infrastructure.JpaAPProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataAPProfileRepository;
import com.projetTransversalIsi.web_application.ap.dto.AbsenceDetailLigneDTO;
import com.projetTransversalIsi.web_application.ap.dto.AbsenceEtudiantDTO;
import com.projetTransversalIsi.web_application.ap.dto.UeDTO;
import com.projetTransversalIsi.web_application.ap.repository.SpringDataAPAbsencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ap")
@RequiredArgsConstructor
public class APAbsencesController {

    private final SpringDataAPAbsencesRepository absencesRepo;
    private final SpringDataAPProfileRepository apProfileRepo;
    private final AbsenceExcelExportService exportService;

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
                        r.getTotalSeances(),
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

    @GetMapping("/absences/{etudiantId}/detail")
    public ResponseEntity<List<AbsenceDetailLigneDTO>> getAbsenceDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long etudiantId) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(List.of());

        List<AbsenceDetailLigneDTO> result = absencesRepo
                .findDetailAbsencesEtudiant(filiereId, etudiantId)
                .stream()
                .map(r -> new AbsenceDetailLigneDTO(
                        r.getSeanceId(),
                        r.getDate(),
                        r.getHeureDebut() != null ? r.getHeureDebut().toString().substring(0, 5) : null,
                        r.getHeureFin()   != null ? r.getHeureFin()  .toString().substring(0, 5) : null,
                        r.getSalle(),
                        r.getUeLibelle(),
                        r.getUeCode(),
                        Boolean.TRUE.equals(r.getPresent()),
                        r.getJustificatifStatut()))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/absences/export")
    public ResponseEntity<byte[]> exportAbsencesExcel(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long classeId,
            @RequestParam(required = false, defaultValue = "Classe") String classeCode) throws IOException {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.status(403).build();

        byte[] xlsx = exportService.buildExcel(classeId, filiereId);

        String filename = "absences_" + classeCode.replaceAll("[^A-Za-z0-9_\\-]", "_") + ".xlsx";
        String encodedName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }

    private Long resolveApFiliereId(UserPrincipal principal) {
        if (principal == null) return null;
        return apProfileRepo.findByUserId(principal.userId())
                .map(JpaAPProfileEntity::getFiliereId)
                .orElse(null);
    }
}
