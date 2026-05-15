package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.AbsenceEtudiantDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.InfoPresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoPresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import com.projetTransversalIsi.justificatif.domain.model.Justificatif;
import com.projetTransversalIsi.justificatif.domain.repository.JustificatifRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAbsencesEtudiantUCImpl implements GetAbsencesEtudiantUC {

    private final PresenceRowRepository presenceRowRepo;
    private final PresenceListRepository presenceListRepo;
    private final InfoPresenceRowRepository infoPresenceRowRepo;
    private final SeanceRepository seanceRepo;
    private final JustificatifRepository justificatifRepo;

    @Override
    public List<AbsenceEtudiantDTO> execute(Long etudiantId) {
        // present=false  → totalement absent (toutes les InfoPresenceRow à false)
        // present=null   → partiellement absent (résultats mixtes ou appels encore ouverts)
        // present=true   → présent → exclu
        List<PresenceRow> absentRows = presenceRowRepo.findByEtudiantId(etudiantId)
                .stream()
                .filter(row -> !Boolean.TRUE.equals(row.getPresent()))
                .collect(Collectors.toList());

        if (absentRows.isEmpty()) return List.of();

        // Charge les PresenceLists référencées (non supprimées)
        Map<Long, PresenceList> presenceListMap = new HashMap<>();
        for (PresenceRow row : absentRows) {
            presenceListRepo.findById(row.getPresenceListId()).ifPresent(pl -> {
                if (!pl.isDeleted()) presenceListMap.put(pl.getId(), pl);
            });
        }

        // Charge les InfoPresenceRows pour calculer les heures
        List<Long> rowIds = absentRows.stream().map(PresenceRow::getId).collect(Collectors.toList());
        Map<Long, List<InfoPresenceRow>> infoRowsByRowId = infoPresenceRowRepo.findByPresenceRowIdIn(rowIds)
                .stream()
                .collect(Collectors.groupingBy(InfoPresenceRow::getPresenceRowId));

        // Charge les Seances référencées
        Set<Long> seanceIds = presenceListMap.values().stream()
                .map(PresenceList::getSeanceId).collect(Collectors.toSet());
        Map<Long, Seance> seanceMap = new HashMap<>();
        for (Long seanceId : seanceIds) {
            seanceRepo.findById(seanceId).ifPresent(s -> seanceMap.put(s.getId(), s));
        }

        // Indexe les justificatifs par séance : seanceId → liste de justificatifs couvrant cette séance.
        // Un justificatif peut référencer plusieurs séances, donc on parcourt seanceIds de chaque justificatif.
        Map<Long, List<Justificatif>> justifsBySeanceId = new HashMap<>();
        for (Justificatif j : justificatifRepo.findByEtudiantId(etudiantId)) {
            for (Long sid : j.getSeanceIds()) {
                justifsBySeanceId.computeIfAbsent(sid, k -> new ArrayList<>()).add(j);
            }
        }

        return absentRows.stream()
                .filter(row -> presenceListMap.containsKey(row.getPresenceListId()))
                .map(row -> buildDTO(row, presenceListMap, infoRowsByRowId, seanceMap, justifsBySeanceId))
                .sorted(Comparator.comparing(AbsenceEtudiantDTO::dateSeance).reversed())
                .collect(Collectors.toList());
    }

    private AbsenceEtudiantDTO buildDTO(
            PresenceRow row,
            Map<Long, PresenceList> presenceListMap,
            Map<Long, List<InfoPresenceRow>> infoRowsByRowId,
            Map<Long, Seance> seanceMap,
            Map<Long, List<Justificatif>> justifsBySeanceId) {

        PresenceList pl = presenceListMap.get(row.getPresenceListId());
        List<InfoPresenceRow> infoRows = infoRowsByRowId.getOrDefault(row.getId(), List.of());
        Seance seance = seanceMap.get(pl.getSeanceId());
        String matiere = seance != null ? seance.getLibelle() : "—";

        int totalMinutes;
        int absentMinutes;

        if (!infoRows.isEmpty()) {
            totalMinutes = infoRows.stream()
                    .mapToInt(ir -> (int) Duration.between(ir.getHeureDebut(), ir.getHeureFin()).toMinutes())
                    .sum();
            absentMinutes = infoRows.stream()
                    .filter(ir -> Boolean.FALSE.equals(ir.getIsPresent()))
                    .mapToInt(ir -> (int) Duration.between(ir.getHeureDebut(), ir.getHeureFin()).toMinutes())
                    .sum();
        } else if (seance != null) {
            totalMinutes  = (int) Duration.between(seance.getHeureDebut(), seance.getHeureFin()).toMinutes();
            absentMinutes = totalMinutes;
        } else {
            totalMinutes  = 0;
            absentMinutes = 0;
        }

        // Plusieurs justificatifs possibles pour une même séance : on prend le plus pertinent.
        List<Justificatif> candidates = justifsBySeanceId.getOrDefault(pl.getSeanceId(), List.of());
        Justificatif bestJustif = pickBest(candidates);

        return new AbsenceEtudiantDTO(
                row.getId(), pl.getId(), pl.getSeanceId(), pl.getDate(),
                matiere, absentMinutes, totalMinutes,
                resolveStatut(bestJustif),
                bestJustif != null ? bestJustif.getId() : null,
                bestJustif != null ? bestJustif.getCommentaireAP() : null
        );
    }

    // Priorité : PENDING > APPROVED > REJECTED (le plus actionnable d'abord).
    private Justificatif pickBest(List<Justificatif> candidates) {
        if (candidates == null || candidates.isEmpty()) return null;
        return candidates.stream()
                .max(Comparator.comparingInt(j -> statutPriority(j.getStatut())))
                .orElse(null);
    }

    private int statutPriority(Justificatif.Statut s) {
        return switch (s) {
            case PENDING  -> 2;
            case APPROVED -> 1;
            case REJECTED -> 0;
        };
    }

    private String resolveStatut(Justificatif j) {
        if (j == null) return "NON_JUSTIFIEE";
        return switch (j.getStatut()) {
            case PENDING  -> "EN_COURS";
            case APPROVED -> "JUSTIFIEE";
            case REJECTED -> "REJETEE";
        };
    }
}
