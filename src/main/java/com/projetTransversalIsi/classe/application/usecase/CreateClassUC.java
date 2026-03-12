package com.projetTransversalIsi.classe.application.usecase;


import com.projetTransversalIsi.classe.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.classe.domain.Classe;

public interface CreateClassUC {
    Classe execute(CreateClassRequestDTO command);
}