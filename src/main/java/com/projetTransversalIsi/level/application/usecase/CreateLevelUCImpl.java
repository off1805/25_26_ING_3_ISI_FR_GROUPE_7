package com.projetTransversalIsi.level.application.usecase;

import com.projetTransversalIsi.level.application.dto.CreateLevelRequestDTO;
import com.projetTransversalIsi.level.domain.level;
import com.projetTransversalIsi.level.domain.levelRepository;
import com.projetTransversalIsi.level.domain.exception.LevelAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateLevelUCImpl implements CreateLevelUC {

    private final levelRepository levelRepo;

    @Transactional
    @Override
    public level execute(CreateLevelRequestDTO command) {
        if(levelRepo.levelAlreadyExists(command.nom())) {
           throw new LevelAlreadyExistsException(command.nom());
        }

        level newLevel = new level();
        newLevel.setNom(command.nom());
        newLevel.setDescription(command.description());

        return levelRepo.save(newLevel);
    }
}
