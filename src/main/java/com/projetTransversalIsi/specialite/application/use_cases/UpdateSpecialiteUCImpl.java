package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.application.dto.UpdateSpecialiteRequestDTO;
import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import com.projetTransversalIsi.specialite.domain.exceptions.SpecialiteNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateSpecialiteUCImpl implements UpdateSpecialiteUC {

    private final SpecialiteRepository specialiteRepository;

    @Transactional
    @Override
    public Specialite execute(Long id, UpdateSpecialiteRequestDTO command) {
        Specialite specialite = specialiteRepository.findById(id)
                .orElseThrow(() -> new SpecialiteNotFoundException(id));

        specialite.setLibelle(command.libelle());
        specialite.setDescription(command.description());
        specialite.setBrancheCode(command.brancheCode().toUpperCase());
        specialite.setNiveauMinimum(command.niveauMinimum());

        return specialiteRepository.save(specialite);
    }
}
