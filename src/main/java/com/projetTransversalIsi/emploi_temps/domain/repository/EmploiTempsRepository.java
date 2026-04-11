package com.projetTransversalIsi.emploi_temps.domain.repository;

import com.projetTransversalIsi.emploi_temps.application.dto.SearchEmploiTempsRequestDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.EmploiTemps;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmploiTempsRepository {

    EmploiTemps save(EmploiTemps emploiTemps);
    Optional<EmploiTemps> findById(Long id);
    List<EmploiTemps> findAll();
    void delete(EmploiTemps emploiTemps);

    Optional<EmploiTemps> findActiveById(Long id);
    List<EmploiTemps> findAllActive();
    List<EmploiTemps> findAllDeleted();

    List<EmploiTemps> findByClasseId(Long classeId);
    List<EmploiTemps> findByPeriode(LocalDate date);
    List<EmploiTemps> findBySemaine(Integer semaine);
    Page<EmploiTemps> findAll(SearchEmploiTempsRequestDTO comand, Pageable page);
    boolean existsEmploiForPeriode(Long classeId, LocalDate dateDebut, LocalDate dateFin);
}
