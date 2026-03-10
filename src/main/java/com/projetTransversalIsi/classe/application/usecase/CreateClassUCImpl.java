package com.projetTransversalIsi.classe.application.usecase;


import com.projetTransversalIsi.classe.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.classe.domain.Classe;
import com.projetTransversalIsi.classe.domain.classeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateClassUCImpl implements CreateClassUC {

    private final classeRepository classerepo;


    @Transactional
    @Override
    public Classe execute(CreateClassRequestDTO command) {
        return null;
    }



}
