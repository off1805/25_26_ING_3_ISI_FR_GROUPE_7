package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaNiveauEntity;
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
    List<JpaNiveauEntity> findByFiliereIdAndDeletedFalse(Long filiereId);
}
