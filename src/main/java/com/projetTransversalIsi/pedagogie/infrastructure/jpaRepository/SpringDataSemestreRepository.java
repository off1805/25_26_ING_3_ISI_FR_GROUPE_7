package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;

import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaSemestreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataSemestreRepository extends JpaRepository<JpaSemestreEntity, Long> {

    List<JpaSemestreEntity> findByAnneeScolaireIdAndNiveauIdOrderByNumeroAsc(
            Long anneeScolaireId,
            Long niveauId
    );

    Optional<JpaSemestreEntity> findByAnneeScolaireIdAndNiveauIdAndNumero(
            Long anneeScolaireId,
            Long niveauId,
            Integer numero
    );

    boolean existsByAnneeScolaireIdAndNiveauIdAndNumero(
            Long anneeScolaireId,
            Long niveauId,
            Integer numero
    );
}