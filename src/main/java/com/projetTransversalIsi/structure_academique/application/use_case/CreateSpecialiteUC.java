package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.CreateSpecialiteRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Specialite;
import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.domain.repository.SpecialiteRepository;
import com.projetTransversalIsi.structure_academique.domain.repository.NiveauRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.SpecialiteAlreadyExistsException;
import com.projetTransversalIsi.structure_academique.domain.exception.NiveauNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSpecialiteUC {

    private final SpecialiteRepository specialiteRepository;
    private final NiveauRepository niveauRepository;

    @Transactional
    public Specialite execute(CreateSpecialiteRequestDTO command) {
        if (specialiteRepository.existsByCode(command.code())) {
            throw new SpecialiteAlreadyExistsException(command.code());
        }

        Niveau niveau = niveauRepository.findActiveById(command.niveauId())
                .orElseThrow(() -> new NiveauNotFoundException(command.niveauId()));

        Specialite specialite = new Specialite(
                command.code().toUpperCase(),
                command.libelle(),
                command.description(),
                niveau
        );

        return specialiteRepository.save(specialite);
    }
}
