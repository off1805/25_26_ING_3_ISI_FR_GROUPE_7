package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository.SpringDataClasseRepository;
import com.projetTransversalIsi.user.profil.infrastructure.JpaAPProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataAPProfileRepository;
import com.projetTransversalIsi.web_application.ap.dto.ClasseOptionDTO;
import com.projetTransversalIsi.web_application.ap.dto.SeanceApDTO;
import com.projetTransversalIsi.web_application.ap.dto.SeanceApMapper;
import com.projetTransversalIsi.web_application.ap.dto.SeancePresenceDetailDTO;
import com.projetTransversalIsi.web_application.ap.dto.SeancePresenceEtudiantDTO;
import com.projetTransversalIsi.web_application.ap.dto.SeancesSemaineDTO;
import com.projetTransversalIsi.web_application.ap.repository.SeanceApRow;
import com.projetTransversalIsi.web_application.ap.repository.SpringDataAPSeancesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RestController
@RequestMapping("/api/ap/seances")
@RequiredArgsConstructor
public class APSeancesController {

    private final SpringDataAPSeancesRepository seancesRepo;
    private final SpringDataAPProfileRepository apProfileRepo;
    private final SpringDataClasseRepository classeRepo;
    private final SeancePresenceExcelExportService presenceExportService;

    @GetMapping("/week")
    public ResponseEntity<SeancesSemaineDTO> getSeancesSemaine(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false, defaultValue = "0") int offset) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(new SeancesSemaineDTO(null, null, List.of()));

        LocalDate lundi = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(offset);
        LocalDate dimanche = lundi.plusDays(6);

        List<SeanceApDTO> seances = seancesRepo.findSeancesSemaine(filiereId, lundi, dimanche)
                .stream()
                .map(SeanceApMapper::toDto)
                .toList();

        return ResponseEntity.ok(new SeancesSemaineDTO(lundi, dimanche, seances));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<SeanceApDTO>> getSeancesHistorique(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Long classeId,
            @PageableDefault(size = 10) Pageable pageable) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(Page.empty(pageable));

        LocalDate lundi = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Page<SeanceApDTO> result = seancesRepo
                .findSeancesHistorique(filiereId, lundi, classeId, pageable)
                .map(SeanceApMapper::toDto);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{seanceId}/presence")
    public ResponseEntity<SeancePresenceDetailDTO> getPresenceDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long seanceId) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.status(403).build();

        SeanceApRow row = seancesRepo.findSeanceById(seanceId, filiereId).orElse(null);
        if (row == null) return ResponseEntity.notFound().build();

        List<SeancePresenceEtudiantDTO> etudiants = row.getPresenceListId() != null
                ? seancesRepo.findPresenceEtudiants(row.getPresenceListId())
                        .stream()
                        .map(r -> new SeancePresenceEtudiantDTO(
                                r.getEtudiantId(), r.getNom(), r.getPrenom(), r.getMatricule(), r.getPresent()))
                        .toList()
                : List.of();

        List<String> creneaux = HeureSlotHelper.computeCreneaux(row.getHeureDebut(), row.getHeureFin());

        return ResponseEntity.ok(new SeancePresenceDetailDTO(SeanceApMapper.toDto(row), row.getPresenceListId(), creneaux, etudiants));
    }

    @GetMapping("/{seanceId}/presence/export")
    public ResponseEntity<byte[]> exportPresenceExcel(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long seanceId) throws java.io.IOException {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.status(403).build();

        SeanceApRow row = seancesRepo.findSeanceById(seanceId, filiereId).orElse(null);
        if (row == null) return ResponseEntity.notFound().build();

        List<SeancePresenceEtudiantDTO> etudiants = row.getPresenceListId() != null
                ? seancesRepo.findPresenceEtudiants(row.getPresenceListId())
                        .stream()
                        .map(r -> new SeancePresenceEtudiantDTO(
                                r.getEtudiantId(), r.getNom(), r.getPrenom(), r.getMatricule(), r.getPresent()))
                        .toList()
                : List.of();

        byte[] xlsx = presenceExportService.buildExcel(SeanceApMapper.toDto(row), etudiants);

        String filename = "presence_" + (row.getClasseCode() != null ? row.getClasseCode() : "seance")
                + "_" + row.getDateSeance() + ".xlsx";
        String encodedName = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .contentType(org.springframework.http.MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx);
    }

    @GetMapping("/classes")
    public ResponseEntity<List<ClasseOptionDTO>> getClasses(
            @AuthenticationPrincipal UserPrincipal principal) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(List.of());

        List<ClasseOptionDTO> result = classeRepo.findBySpecialite_Niveau_Filiere_Id(filiereId)
                .stream()
                .map(c -> new ClasseOptionDTO(c.getId(), c.getCode()))
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
