package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.UpdateSpecialiteRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Specialite;
import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.domain.repository.SpecialiteRepository;
import com.projetTransversalIsi.structure_academique.domain.repository.NiveauRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.SpecialiteNotFoundException;
import com.projetTransversalIsi.structure_academique.domain.exception.NiveauNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateSpecialiteUC {

    private final SpecialiteRepository specialiteRepository;
    private final NiveauRepository niveauRepository;

    @Transactional
    public Specialite execute(Long id, UpdateSpecialiteRequestDTO command) {
        Specialite specialite = specialiteRepository.findById(id)
                .orElseThrow(() -> new SpecialiteNotFoundException(id));

        Niveau niveau = niveauRepository.findActiveById(command.niveauId())
                .orElseThrow(() -> new NiveauNotFoundException(command.niveauId()));

        specialite.setLibelle(command.libelle());
        specialite.setDescription(command.description());
        specialite.setNiveau(niveau);

        return specialiteRepository.save(specialite);
    }
}
