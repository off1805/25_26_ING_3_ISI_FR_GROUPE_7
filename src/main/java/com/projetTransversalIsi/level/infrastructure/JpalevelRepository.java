package com.projetTransversalIsi.level.infrastructure;

import com.projetTransversalIsi.level.domain.level;
import com.projetTransversalIsi.level.domain.levelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaLevelRepository implements levelRepository {

    private final SpringdataLevelRepository jpaRepo;

    @Override
    public level save(level level) {
        JpaLevelEntity entity = new JpaLevelEntity();
        entity.setId(level.getId());
        entity.setNom(level.getNom());
        entity.setDescription(level.getDescription());
        
        JpaLevelEntity savedEntity = jpaRepo.save(entity);
        
        log.info("Level sauvegardé : {}", savedEntity.getNom());
        
        level result = new level();
        result.setId(savedEntity.getId());
        result.setNom(savedEntity.getNom());
        result.setDescription(savedEntity.getDescription());
        
        return result;
    }

    @Override
    public boolean levelAlreadyExists(String nom) {
        return jpaRepo.existsByNom(nom);
    }
}
