package com.projetTransversalIsi.pedagogie.application.services;

import com.projetTransversalIsi.pedagogie.application.dto.ActivateAnneeScolaireResultDTO;
import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;

import java.util.List;
import java.util.Optional;

public interface AnneeScolaireService {
    AnneeScolaire register(CreateAnneeScolaireRequestDTO command);
    List<AnneeScolaire> getAll();
    ActivateAnneeScolaireResultDTO activate(Long id);
    Optional<AnneeScolaire> getActive();
}
