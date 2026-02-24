package com.projetTransversalIsi.classe.application.usecase;


import com.projetTransversalIsi.classe.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.classe.domain.classe;

public interface CreateClassUC {
    classe execute(CreateClassRequestDTO command);
}