package com.projetTransversalIsi.classe.infrastructure;

import com.projetTransversalIsi.classe.domain.classe;
import com.projetTransversalIsi.classe.domain.classeRepository;
import com.projetTransversalIsi.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaClasseRepository implements classeRepository {

    private final SpringdataClasseRepository jpaRepo;

    @Override
    public classe save(classe classe, Set<User> students) {
      
        JpaClasseEntity entity = new JpaClasseEntity();
        entity.setId(classe.getId());
        entity.setNom(classe.getNom());
        entity.setEffectif(classe.getEffectif());
        
    
        JpaClasseEntity savedEntity = jpaRepo.save(entity);
        
        log.info("Classe sauvegardée");
        
     
        classe result = new classe();
        result.setId(savedEntity.getId());
        result.setNom(savedEntity.getNom());
        result.setEffectif(savedEntity.getEffectif());
        
        return result;
    }

    @Override
    public boolean classeAlreadyExists(String nom) {
        return jpaRepo.existsByNom(nom);
    }
}
