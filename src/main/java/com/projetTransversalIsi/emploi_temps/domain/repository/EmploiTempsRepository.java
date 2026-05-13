package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.application.dto.SearchEmploiTempsRequestDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.EmploiTemps;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Port secondaire (driven port) de l'architecture hexagonale :
// le domaine déclare le contrat ; l'infrastructure (JpaEmploiTempsRepository) l'implémente.
// Cela permet de tester la logique métier avec un faux repository sans démarrer Spring.
public interface EmploiTempsRepository {

    EmploiTemps save(EmploiTemps emploiTemps);
    // findById retourne aussi les supprimés (soft-delete) ; préférer findActiveById en lecture normale.
    Optional<EmploiTemps> findById(Long id);
    List<EmploiTemps> findAll();
    // delete = suppression physique (hard), utilisé uniquement lors du remplacement de séances (updateWithSeances).
    void delete(EmploiTemps emploiTemps);

    // Variantes filtrant deleted=false, utiles pour les vues utilisateur.
    Optional<EmploiTemps> findActiveById(Long id);
    List<EmploiTemps> findAllActive();
    List<EmploiTemps> findAllDeleted();

    List<EmploiTemps> findByClasseId(Long classeId);
    // findByPeriode : retourne les emplois dont la fenêtre couvre la date donnée.
    List<EmploiTemps> findByPeriode(LocalDate date);
    List<EmploiTemps> findBySemaine(Integer semaine);
    // findAll avec critères composables via JpaSpecificationExecutor (voir JpaEmploiTempsSpec).
    Page<EmploiTemps> findAll(SearchEmploiTempsRequestDTO comand, Pageable page);
    // Détecte le chevauchement de période pour une classe avant toute création.
    boolean existsEmploiForPeriode(Long classeId, LocalDate dateDebut, LocalDate dateFin);
}
