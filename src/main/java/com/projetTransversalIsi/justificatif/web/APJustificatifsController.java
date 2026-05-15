package com.projetTransversalIsi.justificatif.web;

import com.projetTransversalIsi.justificatif.application.dto.DecisionJustificatifDTO;
import com.projetTransversalIsi.justificatif.application.dto.JustificatifAPViewDTO;
import com.projetTransversalIsi.justificatif.application.dto.JustificatifResponseDTO;
import com.projetTransversalIsi.justificatif.application.use_cases.ApprouverJustificatifUC;
import com.projetTransversalIsi.justificatif.application.use_cases.RejeterJustificatifUC;
import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.domain.repository.JustificatifRepository;
import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
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
import com.projetTransversalIsi.user.profil.infrastructure.SpringDataStudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ap")
@RequiredArgsConstructor
public class APJustificatifsController {

    private final JustificatifRepository justificatifRepo;
    private final SeanceRepository seanceRepo;
    private final SpringDataAPProfileRepository apProfileRepo;
    private final SpringDataStudentProfileRepository studentProfileRepo;
    private final NiveauService niveauService;
    private final SpecialiteService specialiteService;
    private final ClasseService classeService;
    private final ApprouverJustificatifUC approuverUC;
    private final RejeterJustificatifUC rejeterUC;

    @GetMapping("/justificatifs")
    public ResponseEntity<List<JustificatifAPViewDTO>> getJustificatifsFiliere(
            @AuthenticationPrincipal UserPrincipal principal) {

        Long filiereId = resolveApFiliereId(principal);
        if (filiereId == null) return ResponseEntity.ok(List.of());

        // Résolution : filière → niveaux → spécialités → classes → étudiants
        Map<Long, JpaStudentProfileEntity> profileMap = buildProfileMap(filiereId);
        if (profileMap.isEmpty()) return ResponseEntity.ok(List.of());

        // Tous les justificatifs des étudiants de la filière, enrichis
        List<JustificatifAPViewDTO> result = new ArrayList<>();
        for (Map.Entry<Long, JpaStudentProfileEntity> entry : profileMap.entrySet()) {
            Long etudiantId = entry.getKey();
            JpaStudentProfileEntity profile = entry.getValue();

            for (Justificatif j : justificatifRepo.findByEtudiantId(etudiantId)) {
                result.add(toView(j, profile));
            }
        }

        result.sort(Comparator.comparing(JustificatifAPViewDTO::createdAt,
                Comparator.nullsLast(Comparator.reverseOrder())));

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/justificatifs/approuver")
    public ResponseEntity<JustificatifResponseDTO> approuver(@RequestBody DecisionJustificatifDTO dto) {
        return ResponseEntity.ok(approuverUC.execute(dto));
    }

    @PatchMapping("/justificatifs/rejeter")
    public ResponseEntity<JustificatifResponseDTO> rejeter(@RequestBody DecisionJustificatifDTO dto) {
        return ResponseEntity.ok(rejeterUC.execute(dto));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Long resolveApFiliereId(UserPrincipal principal) {
        if (principal == null) return null;
        return apProfileRepo.findByUserId(principal.userId())
                .map(JpaAPProfileEntity::getFiliereId)
                .orElse(null);
    }

    private Map<Long, JpaStudentProfileEntity> buildProfileMap(Long filiereId) {
        Map<Long, JpaStudentProfileEntity> map = new LinkedHashMap<>();
        List<NiveauResponseDTO> niveaux = niveauService.getNiveauxByFiliereId(filiereId);
        for (NiveauResponseDTO n : niveaux) {
            for (SpecialiteResponseDTO s : specialiteService.getSpecialitesByNiveauId(n.id())) {
                for (ClasseResponseDTO c : classeService.getClassesBySpecialiteId(s.id())) {
                    for (JpaStudentProfileEntity p : studentProfileRepo.findByClasseId(c.id())) {
                        map.putIfAbsent(p.getId(), p);
                    }
                }
            }
        }
        return map;
    }

    private JustificatifAPViewDTO toView(Justificatif j, JpaStudentProfileEntity profile) {
        List<JustificatifAPViewDTO.SeanceInfo> seances = j.getSeanceIds().stream()
                .map(sid -> seanceRepo.findById(sid)
                        .map(s -> new JustificatifAPViewDTO.SeanceInfo(
                                sid,
                                s.getDateSeance() != null ? s.getDateSeance().toString() : "",
                                s.getLibelle()))
                        .orElse(new JustificatifAPViewDTO.SeanceInfo(sid, "", "—")))
                .collect(Collectors.toList());

        List<JustificatifAPViewDTO.FichierInfo> fichiers = j.getFichiers().stream()
                .map(f -> new JustificatifAPViewDTO.FichierInfo(f.getFichierUrl(), f.getNomOriginal()))
                .collect(Collectors.toList());

        return new JustificatifAPViewDTO(
                j.getId(),
                j.getEtudiantId(),
                profile.getNom(),
                profile.getPrenom(),
                profile.getMatricule(),
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
