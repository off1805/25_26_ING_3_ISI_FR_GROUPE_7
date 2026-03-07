package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.application.dto.CreateSpecialiteRequestDTO;
import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import com.projetTransversalIsi.specialite.domain.exceptions.SpecialiteAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSpecialiteUCImpl implements CreateSpecialiteUC {

    private final SpecialiteRepository specialiteRepository;

    @Transactional
    @Override
    public Specialite execute(CreateSpecialiteRequestDTO command) {
        if (specialiteRepository.existsByCode(command.code())) {
            throw new SpecialiteAlreadyExistsException(command.code());
        }

        Specialite specialite = new Specialite(
                command.code().toUpperCase(),
                command.libelle(),
                command.description(),
                command.brancheCode().toUpperCase(),
                command.niveauMinimum()
        );

        return specialiteRepository.save(specialite);
    }
}
