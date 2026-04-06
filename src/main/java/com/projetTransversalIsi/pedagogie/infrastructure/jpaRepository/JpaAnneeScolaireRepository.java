package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;

import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import com.projetTransversalIsi.pedagogie.infrastructure.AnneeScolaireMapper;
import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaAnneeScolaireEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaAnneeScolaireRepository implements AnneeScolaireRepository {

    private final SpringDataAnneeScolaireRepository sprgRepo;
    private final AnneeScolaireMapper anneeMaper;

    @Override
    public AnneeScolaire save(AnneeScolaire anneeScolaire) {
        JpaAnneeScolaireEntity jpaEntity = anneeMaper.toJpaEntity(anneeScolaire);
        return anneeMaper.toDomainModel(sprgRepo.save(jpaEntity));
    }

    @Override
    public Optional<AnneeScolaire> findById(Long id) {
        return sprgRepo.findById(id)
                .map(anneeMaper::toDomainModel);
    }
}
