package com.projetTransversalIsi.pedagogie.domain;

import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;

import java.util.List;
import java.util.Optional;

public interface AnneeScolaireRepository {

    AnneeScolaire save(AnneeScolaire anneeScolaire);

    Optional<AnneeScolaire> findById(Long id);

    List<AnneeScolaire> findAll();

    Optional<AnneeScolaire> findActive();
}
