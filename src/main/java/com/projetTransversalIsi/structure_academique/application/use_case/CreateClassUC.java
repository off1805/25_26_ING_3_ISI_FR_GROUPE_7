package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.CreateClassRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Classe;
import com.projetTransversalIsi.structure_academique.domain.model.Specialite;
import com.projetTransversalIsi.structure_academique.domain.repository.ClasseRepository;
import com.projetTransversalIsi.structure_academique.domain.repository.SpecialiteRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.SpecialiteNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateClassUC {

    private final ClasseRepository classerepo;
    private final SpecialiteRepository specialiteRepository;

    @Transactional
    public Classe execute(CreateClassRequestDTO command) {
        Specialite specialite = specialiteRepository.findById(command.specialiteId())
                .orElseThrow(() -> new SpecialiteNotFoundException(command.specialiteId()));
        
        Classe classe = new Classe(
                command.code().toUpperCase(),
                command.description(),
                specialite
        );

        return classerepo.save(classe);
    }
}
