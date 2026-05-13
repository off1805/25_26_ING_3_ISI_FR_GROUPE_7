package com.projetTransversalIsi.emploi_temps.application.service;

import com.projetTransversalIsi.emploi_temps.application.dto.*;
import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import com.projetTransversalIsi.emploi_temps.domain.exceptions.SeanceConflictException;
import com.projetTransversalIsi.emploi_temps.domain.exceptions.SeanceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeanceService {

    private final SeanceRepository seanceRepo;

    // Crée une séance de cours autonome (hors flux createEmploiTempsWithSeances).
    // Vérifie le conflit horaire de l'enseignant avant insertion.
    // Normalise libelle et salle pour respecter la contrainte NOT NULL en base.
    @Transactional
    public SeanceResponseDTO createSeance(CreateSeanceRequestDTO request) {
        if (seanceRepo.existsConflict(
                request.enseignantId(),
                request.dateSeance(),
                request.heureDebut(),
                request.heureFin())) {
            throw new SeanceConflictException(
                    "L'enseignant a déjà une séance programmée à cette période"
            );
        }

        String libelle = (request.libelle() == null || request.libelle().isBlank())
                ? "Séance"
                : request.libelle();

        String salle = (request.salle() == null || request.salle().isBlank())
                ? "Salle 1"
                : request.salle();

        Seance seance = new Seance(
                libelle,
                salle,
                request.dateSeance(),
                request.heureDebut(),
                request.heureFin(),
                request.coursId(),
                request.enseignantId()
        );

        return SeanceResponseDTO.fromDomain(seanceRepo.save(seance));
    }

    // update ne modifie que les horaires (voir Seance.update) ; dateSeance et salle sont fixes
    // pour ne pas invalider les présences déjà enregistrées sur cette séance.
    @Transactional
    public SeanceResponseDTO updateSeance(UpdateSeanceRequestDTO request) {
        Seance seance = seanceRepo.findById(request.id())
                .orElseThrow(() -> new SeanceNotFoundException(request.id()));

        seance.update(
                request.heureDebut(),
                request.heureFin()
        );

        return SeanceResponseDTO.fromDomain(seanceRepo.save(seance));
    }

    @Transactional
    public void deleteSeance(Long id) {
        Seance seance = seanceRepo.findById(id)
                .orElseThrow(() -> new SeanceNotFoundException(id));

        if (seance.isDeleted()) {
            throw new IllegalStateException("La séance " + id + " est déjà supprimée");
        }

        seance.delete();
        seanceRepo.save(seance);
    }

    public SeanceResponseDTO getSeanceById(Long id) {
        return seanceRepo.findById(id)
                .map(SeanceResponseDTO::fromDomain)
                .orElseThrow(() -> new SeanceNotFoundException(id));
    }

    // Recherche par critère unique avec priorité : date > enseignant > cours > tout.
    // Le filtre includeDeleted est appliqué en mémoire après la requête SQL car le repo
    // ne supporte pas encore ce filtre en JPQL sur ces méthodes de recherche.
    public List<SeanceResponseDTO> searchSeances(SearchSeanceRequestDTO criteria) {
        List<Seance> resultats;

        if (criteria.date() != null) {
            resultats = seanceRepo.findByDate(criteria.date());
        } else if (criteria.enseignantId() != null) {
            resultats = seanceRepo.findByEnseignantId(criteria.enseignantId());
        } else if (criteria.coursId() != null) {
            resultats = seanceRepo.findByCoursId(criteria.coursId());
        } else {
            resultats = seanceRepo.findAll();
        }

        if (criteria.includeDeleted() != null && !criteria.includeDeleted()) {
            resultats = resultats.stream()
                    .filter(s -> !s.isDeleted())
                    .collect(Collectors.toList());
        }

        return resultats.stream()
                .map(SeanceResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    // Utilisé par TeacherController pour afficher les séances du jour d'un enseignant.
    // Le filtre par enseignantId est appliqué en mémoire sur le résultat de findByDate
    // car le repo n'expose pas de méthode combinant date ET enseignantId.
    public List<Seance> getSeancesTodayByEnseignant(Long enseignantId, Boolean includeDeleted) {
        if (enseignantId == null) {
            return Collections.emptyList();
        }

        LocalDate today = LocalDate.now();

        List<Seance> result = seanceRepo.findByDate(today).stream()
                .filter(s -> Objects.equals(s.getEnseignantId(), enseignantId))
                .collect(Collectors.toList());

        if (includeDeleted != null && !includeDeleted) {
            result = result.stream()
                    .filter(s -> !s.isDeleted())
                    .collect(Collectors.toList());
        }

        return result;
    }
}
