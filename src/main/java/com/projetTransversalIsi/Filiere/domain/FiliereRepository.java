package com.projetTransversalIsi.Filiere.domain;

import com.projetTransversalIsi.Filiere.application.dto.SearchFiliereRequestDTO;

import java.util.List;
import java.util.Optional;

public interface FiliereRepository {


    Filiere save(Filiere filiere);
    Optional<Filiere> findById(Long id);
    List<Filiere> findAll();
    void delete(Filiere filiere);


    Optional<Filiere> findActiveById(Long id);
    List<Filiere> findAllActive();
    List<Filiere> findAllDeleted();

    List<Filiere> search(String code,String name);

    Optional<Filiere> findByCode(String code);
    List<Filiere> searchByNom(String nom);
    boolean existsByCode(String code);
    boolean existsActiveByCode(String code);
    List<Filiere> findByCycleId(Long cycleId);
    List<Filiere> search(SearchFiliereRequestDTO command);
}
