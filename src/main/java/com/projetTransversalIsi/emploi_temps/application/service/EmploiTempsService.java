package com.projetTransversalIsi.emploi_temps.application.service;

import com.projetTransversalIsi.emploi_temps.application.dto.*;
import com.projetTransversalIsi.emploi_temps.domain.model.EmploiStatus;
import com.projetTransversalIsi.emploi_temps.domain.model.EmploiTemps;
import com.projetTransversalIsi.emploi_temps.domain.model.Seance;
import com.projetTransversalIsi.emploi_temps.domain.repository.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import com.projetTransversalIsi.emploi_temps.domain.exceptions.EmploiTempsConflictException;
import com.projetTransversalIsi.emploi_temps.domain.exceptions.EmploiTempsNotFoundException;
import com.projetTransversalIsi.emploi_temps.domain.exceptions.SeanceConflictException;
import com.projetTransversalIsi.emploi_temps.domain.exceptions.SeanceNotFoundException;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploiTempsService {

    private final EmploiTempsRepository emploiTempsRepo;
    private final SeanceRepository seanceRepo;
    private final AnneeScolaireRepository anneeScolaireRepository;

    // Crée un emploi du temps vide (sans séances) pour une classe.
    // Double garde : pas de chevauchement de période ET pas de date passée.
    // Le status UPCOMING est fixé ici ; un job ou la couche de lecture recalcule PAST/ONGOING dynamiquement.
    @Transactional
    public EmploiTempsResponseDTO createEmploiTemps(CreateEmploiTempsRequestDTO request) {
        if (emploiTempsRepo.existsEmploiForPeriode(
                request.classeId(),
                request.dateDebut(),
                request.dateFin())) {
            throw new EmploiTempsConflictException("Un emploi du temps existe déjà pour cette période");
        }
        if(request.dateDebut().isBefore(LocalDate.now())){
            throw new EmploiTempsConflictException("Vous ne pouvez pas creer un emploi de temps pour une periode deja passee.");
        }
        EmploiTemps emploiTemps= EmploiTemps.builder()
                .dateDebut(request.dateDebut())
                .dateFin(request.dateFin())
                .semaine(request.semaine())
                .classeId(request.classeId())
                .anneeScolaireId(activeAnneeScolaireId())
                .status(EmploiStatus.UPCOMING)
                .build();
        return EmploiTempsResponseDTO.fromDomain(emploiTempsRepo.save(emploiTemps));
    }

    // Mise à jour simple des métadonnées (période, semaine, classe) sans toucher aux séances.
    // Attention : si la nouvelle période exclut des séances existantes, EmploiTemps.addSeance()
    // lancera une exception lors du prochain ajout, mais pas rétroactivement ici.
    @Transactional
    public EmploiTempsResponseDTO updateEmploiTemps(UpdateEmploiTempsRequestDTO request) {
        EmploiTemps emploiTemps = emploiTempsRepo.findById(request.id())
                .orElseThrow(() -> new EmploiTempsNotFoundException(request.id()));

        emploiTemps.update(
                request.dateDebut(),
                request.dateFin(),
                request.semaine(),
                request.classeId()
        );
        return EmploiTempsResponseDTO.fromDomain(emploiTempsRepo.save(emploiTemps));
    }

    // Soft-delete : l'emploi reste en base avec deleted=true pour l'historique.
    // La garde dans EmploiTemps.delete() évite un double-delete accidentel.
    @Transactional
    public void deleteEmploiTemps(Long id) {
        EmploiTemps emploiTemps = emploiTempsRepo.findById(id)
                .orElseThrow(() -> new EmploiTempsNotFoundException(id));

        if (emploiTemps.isDeleted()) {
            throw new IllegalStateException("L'emploi du temps " + id + " est déjà supprimé");
        }
        emploiTemps.delete();
        emploiTempsRepo.save(emploiTemps);
    }

    public EmploiTempsResponseDTO getEmploiTempsById(Long id) {
        return emploiTempsRepo.findById(id)
                .map(EmploiTempsResponseDTO::fromDomain)
                .orElseThrow(() -> new EmploiTempsNotFoundException(id));
    }

    // Délègue la construction de la Specification JPA à JpaEmploiTempsRepository.findAll(),
    // qui compose les prédicats de JpaEmploiTempsSpec selon les critères non-null du DTO.
    public Page<EmploiTempsResponseDTO> searchEmploiTemps(SearchEmploiTempsRequestDTO criteria, Pageable page) {
        return emploiTempsRepo.findAll(criteria,page).map(EmploiTempsResponseDTO::fromDomain);
    }

    // Crée l'emploi du temps ET ses séances en une seule transaction.
    // Chaque séance est persistée via seanceRepo.save() avant d'être ajoutée à l'agrégat,
    // car JPA a besoin de l'id pour construire la relation OneToMany via @JoinColumn.
    @Transactional
    public EmploiTempsResponseDTO createEmploiTempsWithSeances(CreateEmploiTempsWithSeancesDTO command) {
        if (emploiTempsRepo.existsEmploiForPeriode(
                command.classeId(),
                command.dateDebut(),
                command.dateFin())) {
            throw new EmploiTempsConflictException("Un emploi du temps existe déjà pour cette période");
        }

        EmploiTemps emploiTemps = new EmploiTemps(
                command.dateDebut(),
                command.dateFin(),
                command.semaine(),
                command.classeId()
        );
        emploiTemps.setAnneeScolaireId(activeAnneeScolaireId());

        command.seances().forEach(seanceDTO -> {
            Seance.TypeSeance type = seanceDTO.resolvedType();

            // La vérification de conflit ne s'applique qu'aux SEANCE avec enseignant.
            // Les EVENEMENT n'ont pas d'enseignant, donc pas de contrainte d'unicité horaire.
            if (Seance.TypeSeance.SEANCE.equals(type) && seanceDTO.enseignantId() != null) {
                if (seanceRepo.existsConflict(
                        seanceDTO.enseignantId(),
                        seanceDTO.dateSeance(),
                        seanceDTO.heureDebut(),
                        seanceDTO.heureFin())) {
                    throw new SeanceConflictException("L'enseignant a déjà une séance programmée à cette période");
                }
            }

            Seance seance;
            if (Seance.TypeSeance.EVENEMENT.equals(type)) {
                seance = new Seance(
                        seanceDTO.libelle(),
                        seanceDTO.dateSeance(),
                        seanceDTO.heureDebut(),
                        seanceDTO.heureFin(),
                        seanceDTO.couleur(),
                        seanceDTO.iconKey()
                );
            } else {
                // Valeurs par défaut pour libelle/salle afin d'éviter une contrainte NOT NULL en base.
                seance = new Seance(
                        seanceDTO.libelle() != null ? seanceDTO.libelle() : "Séance",
                        seanceDTO.salle() != null ? seanceDTO.salle() : "Salle 1",
                        seanceDTO.dateSeance(),
                        seanceDTO.heureDebut(),
                        seanceDTO.heureFin(),
                        seanceDTO.coursId(),
                        seanceDTO.enseignantId()
                );
                seance.setCouleur(seanceDTO.couleur());
            }
            seance.setAnneeScolaireId(activeAnneeScolaireId());

            // Persiste d'abord la séance pour obtenir son id, puis l'ajoute à l'agrégat.
            emploiTemps.addSeance(seanceRepo.save(seance));
        });

        return EmploiTempsResponseDTO.fromDomain(emploiTempsRepo.save(emploiTemps));
    }

    // Stratégie replace-all : supprime toutes les anciennes séances (hard delete) puis recrée.
    // Plus simple qu'un diff individuel séance-par-séance ; acceptable car l'éditeur drag-drop
    // envoie toujours le lot complet lors d'une sauvegarde.
    @Transactional
    public EmploiTempsResponseDTO updateEmploiTempsWithSeances(UpdateEmploiTempsWithSeancesDTO command) {
        EmploiTemps emploi = emploiTempsRepo.findById(command.id())
                .orElseThrow(() -> new EmploiTempsNotFoundException(command.id()));

        // Hard-delete des séances existantes avant de les remplacer.
        emploi.getSeances().forEach(seanceRepo::delete);
        emploi.getSeances().clear();

        emploi.update(command.dateDebut(), command.dateFin(), command.semaine(), command.classeId());

        command.seances().forEach(seanceDTO -> {
            Seance.TypeSeance type = seanceDTO.resolvedType();

            if (Seance.TypeSeance.SEANCE.equals(type) && seanceDTO.enseignantId() != null) {
                if (seanceRepo.existsConflict(
                        seanceDTO.enseignantId(),
                        seanceDTO.dateSeance(),
                        seanceDTO.heureDebut(),
                        seanceDTO.heureFin())) {
                    throw new SeanceConflictException("Conflit d'emploi du temps enseignant");
                }
            }

            Seance seance;
            if (Seance.TypeSeance.EVENEMENT.equals(type)) {
                seance = new Seance(
                        seanceDTO.libelle(),
                        seanceDTO.dateSeance(),
                        seanceDTO.heureDebut(),
                        seanceDTO.heureFin(),
                        seanceDTO.couleur(),
                        seanceDTO.iconKey()
                );
            } else {
                seance = new Seance(
                        seanceDTO.libelle() != null ? seanceDTO.libelle() : "Séance",
                        seanceDTO.salle() != null ? seanceDTO.salle() : "Salle 1",
                        seanceDTO.dateSeance(),
                        seanceDTO.heureDebut(),
                        seanceDTO.heureFin(),
                        seanceDTO.coursId(),
                        seanceDTO.enseignantId()
                );
                seance.setCouleur(seanceDTO.couleur());
            }
            seance.setAnneeScolaireId(activeAnneeScolaireId());

            emploi.addSeance(seanceRepo.save(seance));
        });

        return EmploiTempsResponseDTO.fromDomain(emploiTempsRepo.save(emploi));
    }

    private Long activeAnneeScolaireId() {
        return anneeScolaireRepository.findActive().map(AnneeScolaire::getId).orElse(null);
    }

    // Lie une séance déjà existante (créée séparément) à un emploi du temps.
    // La validation de période est assurée par EmploiTemps.addSeance().
    @Transactional
    public EmploiTempsResponseDTO addSeanceToEmploi(AddSeanceToEmploiDTO command) {
        EmploiTemps emploiTemps = emploiTempsRepo.findById(command.emploiTempsId())
                .orElseThrow(() -> new EmploiTempsNotFoundException(command.emploiTempsId()));

        Seance seance = seanceRepo.findById(command.seanceId())
                .orElseThrow(() -> new SeanceNotFoundException(command.seanceId()));

        emploiTemps.addSeance(seance);
        return EmploiTempsResponseDTO.fromDomain(emploiTempsRepo.save(emploiTemps));
    }

    // Retire la séance de l'emploi du temps (retire le lien FK emploi_temps_id) sans la supprimer.
    @Transactional
    public EmploiTempsResponseDTO removeSeanceFromEmploi(AddSeanceToEmploiDTO command) {
        log.info("Retrait de la séance {} de l'emploi du temps {}", command.seanceId(), command.emploiTempsId());

        EmploiTemps emploiTemps = emploiTempsRepo.findById(command.emploiTempsId())
                .orElseThrow(() -> new EmploiTempsNotFoundException(command.emploiTempsId()));

        Seance seance = seanceRepo.findById(command.seanceId())
                .orElseThrow(() -> new SeanceNotFoundException(command.seanceId()));

        emploiTemps.removeSeance(seance);
        return EmploiTempsResponseDTO.fromDomain(emploiTempsRepo.save(emploiTemps));
    }
}
