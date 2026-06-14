package com.projetTransversalIsi.web_application.ap;

import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import com.projetTransversalIsi.justificatif.application.dto.JustificatifAPViewDTO;
import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.infrastructure.entity.JpaJustificatifEntity;
import com.projetTransversalIsi.justificatif.infrastructure.repository.SpringDataJustificatifRepository;
import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireResponseDTO;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.infrastructure.AnneeScolaireMapper;
import com.projetTransversalIsi.security.domain.UserPrincipal;
import com.projetTransversalIsi.structure_academique.application.dto.ClasseResponseDTO;
import com.projetTransversalIsi.structure_academique.application.dto.NiveauResponseDTO;
import com.projetTransversalIsi.structure_academique.application.dto.SpecialiteResponseDTO;
import com.projetTransversalIsi.structure_academique.application.service.ClasseService;
import com.projetTransversalIsi.structure_academique.application.service.NiveauService;
import com.projetTransversalIsi.structure_academique.application.service.SpecialiteService;
import com.projetTransversalIsi.user.profil.infrastructure.JpaAPProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.JpaStudentProfileEntity;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataAPProfileRepository;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentClasseHistoryRepository;
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import com.projetTransversalIsi.web_application.ap.dto.AbsenceEtudiantDTO;
import com.projetTransversalIsi.web_application.ap.dto.ClasseHistoryArchiveDTO;
import com.projetTransversalIsi.web_application.ap.dto.SeanceApDTO;
import com.projetTransversalIsi.web_application.ap.dto.SeanceApMapper;
import com.projetTransversalIsi.web_application.ap.dto.UeDTO;
import com.projetTransversalIsi.web_application.ap.repository.SpringDataAPAbsencesRepository;
import com.projetTransversalIsi.web_application.ap.repository.SpringDataAPSeancesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ap/archives")
@RequiredArgsConstructor
public class APArchivesController {

    private final SpringDataAPSeancesRepository seancesRepo;
    private final SpringDataAPAbsencesRepository absencesRepo;
    private final SpringDataJustificatifRepository justificatifRepo;
    private final SpringDataStudentClasseHistoryRepository classeHistoryRepo;
    private final SpringDataAPProfileRepository apProfileRepo;
    private final SpringDataStudentProfileRepository studentProfileRepo;
    private final NiveauService niveauService;
    private final SpecialiteService specialiteService;
    private final ClasseService classeService;
    private final SeanceRepository seanceRepo;
    private final AnneeScolaireRepository anneeScolaireRepo;
    private final AnneeScolaireMapper anneeScolaireMapper;

    @GetMapping("/annees")
    public ResponseEntity<List<CreateAnneeScolaireResponseDTO>> getAnnees() {
        List<CreateAnneeScolaireResponseDTO> result = anneeScolaireRepo.findAll()
                .stream().map(anneeScolaireMapper::toResponseDto).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/seances")
    public ResponseEntity<Page<SeanceApDTO>> getSeancesArchive(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long anneeScolaireId,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @PageableDefault(size = 10) Pageable pageable) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(Page.empty(pageable));

        Page<SeanceApDTO> result = seancesRepo
                .findSeancesArchive(filiereId, anneeScolaireId, classeId, type, parseDate(dateDebut), parseDate(dateFin), pageable)
                .map(SeanceApMapper::toDto);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/absences")
    public ResponseEntity<Page<AbsenceEtudiantDTO>> getAbsencesArchive(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long anneeScolaireId,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Long ueId,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @PageableDefault(size = 10) Pageable pageable) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(Page.empty(pageable));

        Page<AbsenceEtudiantDTO> result = absencesRepo
                .findAbsencesParEtudiantArchive(filiereId, anneeScolaireId, classeId, ueId, parseDate(dateDebut), parseDate(dateFin), pageable)
                .map(r -> new AbsenceEtudiantDTO(
                        r.getEtudiantId(), r.getNom(), r.getPrenom(), r.getMatricule(), r.getPhotoUrl(),
                        r.getClasseId(), r.getClasseCode(), r.getTotalSeances(), r.getTotalAbsences(),
                        r.getNbJustifiees(), r.getNbEnAttente()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/ues")
    public ResponseEntity<List<UeDTO>> getUesArchive(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long anneeScolaireId) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(List.of());

        List<UeDTO> result = absencesRepo.findUesWithPresenceData(filiereId, anneeScolaireId)
                .stream()
                .map(r -> new UeDTO(r.getId(), r.getLibelle(), r.getCode()))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/justificatifs")
    public ResponseEntity<Page<JustificatifAPViewDTO>> getJustificatifsArchive(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long anneeScolaireId,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) String statut,
            @PageableDefault(size = 10) Pageable pageable) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(Page.empty(pageable));

        Map<Long, JpaStudentProfileEntity> profileMap = buildProfileMap(filiereId, classeId);
        if (profileMap.isEmpty()) return ResponseEntity.ok(Page.empty(pageable));

        Justificatif.Statut statutEnum = (statut != null && !statut.isBlank())
                ? Justificatif.Statut.valueOf(statut) : null;

        Page<JustificatifAPViewDTO> result = justificatifRepo
                .findArchive(anneeScolaireId, List.copyOf(profileMap.keySet()), statutEnum, pageable)
                .map(j -> toView(j, profileMap.get(j.getEtudiantId())));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/classe-history")
    public ResponseEntity<Page<ClasseHistoryArchiveDTO>> getClasseHistoryArchive(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long anneeScolaireId,
            @RequestParam(required = false) Long classeId,
            @PageableDefault(size = 10) Pageable pageable) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(Page.empty(pageable));

        Page<ClasseHistoryArchiveDTO> result = classeHistoryRepo
                .findArchive(anneeScolaireId, filiereId, classeId, pageable)
                .map(h -> new ClasseHistoryArchiveDTO(
                        h.getStudent().getId(),
                        h.getStudent().getNom(),
                        h.getStudent().getPrenom(),
                        h.getStudent().getMatricule(),
                        h.getClasse().getCode(),
                        h.getDateDebut(),
                        h.getDateFin()));

        return ResponseEntity.ok(result);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Long resolveApFiliereId(UserPrincipal principal) {
        if (principal == null) return null;
        return apProfileRepo.findByUserId(principal.userId())
                .map(JpaAPProfileEntity::getFiliereId)
                .orElse(null);
    }

    private LocalDate parseDate(String value) {
        return (value != null && !value.isBlank()) ? LocalDate.parse(value) : null;
    }

    private Map<Long, JpaStudentProfileEntity> buildProfileMap(Long filiereId, Long classeId) {
        Map<Long, JpaStudentProfileEntity> map = new LinkedHashMap<>();
        for (NiveauResponseDTO n : niveauService.getNiveauxByFiliereId(filiereId)) {
            for (SpecialiteResponseDTO s : specialiteService.getSpecialitesByNiveauId(n.id())) {
                for (ClasseResponseDTO c : classeService.getClassesBySpecialiteId(s.id())) {
                    if (classeId != null && !classeId.equals(c.id())) continue;
                    for (JpaStudentProfileEntity p : studentProfileRepo.findByClasseId(c.id())) {
                        map.putIfAbsent(p.getId(), p);
                    }
                }
            }
        }
        return map;
    }

    private JustificatifAPViewDTO toView(JpaJustificatifEntity j, JpaStudentProfileEntity profile) {
        List<JustificatifAPViewDTO.SeanceInfo> seances = j.getSeanceIds().stream()
                .map(sid -> seanceRepo.findById(sid)
                        .map(s -> new JustificatifAPViewDTO.SeanceInfo(
                                sid,
                                s.getDateSeance() != null ? s.getDateSeance().toString() : "",
                                s.getLibelle()))
                        .orElse(new JustificatifAPViewDTO.SeanceInfo(sid, "", "—")))
                .toList();

        List<JustificatifAPViewDTO.FichierInfo> fichiers = j.getFichiers().stream()
                .map(f -> new JustificatifAPViewDTO.FichierInfo(f.getFichierUrl(), f.getNomOriginal()))
                .toList();

        return new JustificatifAPViewDTO(
                j.getId(),
                j.getEtudiantId(),
                profile.getNom(),
                profile.getPrenom(),
                profile.getMatricule(),
                profile.getPhotoUrl(),
                seances,
                j.getMotif(),
                j.getMessage(),
                fichiers,
                j.getDateAbsence(),
                j.getStatut().name(),
                j.getCommentaireAP(),
                j.getCreatedAt()
        );
    }
}
