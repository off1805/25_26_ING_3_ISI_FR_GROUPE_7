package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;

import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaSemestreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataSemestreRepository extends JpaRepository<JpaSemestreEntity, Long> {

    List<JpaSemestreEntity> findByAnneeScolaireIdAndSpecialiteIdOrderByNumeroAsc(
            Long anneeScolaireId,
            Long specialiteId
    );

    Optional<JpaSemestreEntity> findByAnneeScolaireIdAndSpecialiteIdAndNumero(
            Long anneeScolaireId,
            Long specialiteId,
            Integer numero
    );

    boolean existsByAnneeScolaireIdAndSpecialiteIdAndNumero(
            Long anneeScolaireId,
            Long specialiteId,
            Integer numero
    );
}