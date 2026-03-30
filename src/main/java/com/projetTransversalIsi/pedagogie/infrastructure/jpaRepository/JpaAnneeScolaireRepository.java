package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;

import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.infrastructure.AnneeScolaireMapper;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaAnneeScolaireEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class JpaAnneeScolaireRepository implements AnneeScolaireRepository {
    final private SpringDataAnneeScolaireRepository sprgRepo;
    final private AnneeScolaireMapper anneeMaper;
    public AnneeScolaire save(AnneeScolaire anneeScolaire){
        System.out.println(anneeScolaire);
        JpaAnneeScolaireEntity jpaEntity= anneeMaper.toJpaEntity(anneeScolaire);
        System.out.println(jpaEntity);
       return anneeMaper.toDomainModel(sprgRepo.save(jpaEntity));
    }
}
