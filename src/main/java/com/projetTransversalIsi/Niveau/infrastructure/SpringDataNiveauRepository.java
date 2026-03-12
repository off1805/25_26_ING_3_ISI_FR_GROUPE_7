package com.projetTransversalIsi.Niveau.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface SpringDataNiveauRepository extends JpaRepository<JpaNiveauEntity, Long>, JpaSpecificationExecutor<JpaNiveauEntity> {

    List<JpaNiveauEntity> findByDeletedFalse();
    List<JpaNiveauEntity> findByDeletedTrue();
    Optional<JpaNiveauEntity> findByIdAndDeletedFalse(Long id);

    boolean existsByOrdreAndFiliereId(int ordre, Long filiereId);
    boolean existsByOrdreAndFiliereIdAndDeletedFalse(int ordre, Long filiereId);
}
