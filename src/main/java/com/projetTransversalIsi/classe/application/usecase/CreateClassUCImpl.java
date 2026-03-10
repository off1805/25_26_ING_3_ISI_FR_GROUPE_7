package com.projetTransversalIsi.classe.application.usecase;


import com.projetTransversalIsi.classe.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.classe.application.usecase.CreateClassUC;

import com.projetTransversalIsi.classe.domain.Classe;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateClassUCImpl implements CreateClassUC {



    @Transactional
    @Override
    public Classe execute(CreateClassRequestDTO command) {
        return null;
    }


}
