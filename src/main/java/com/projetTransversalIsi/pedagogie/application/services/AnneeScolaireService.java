package com.projetTransversalIsi.pedagogie.application.services;

import com.projetTransversalIsi.pedagogie.application.dto.CreateAnneeScolaireRequestDTO;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;

public interface AnneeScolaireService {
    AnneeScolaire register(CreateAnneeScolaireRequestDTO command);
}
