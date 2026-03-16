package com.projetTransversalIsi.emploi_Temps.domain;


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


    boolean existsEmploiForPeriode(Long classeId, LocalDate dateDebut, LocalDate dateFin);
}
