package com.projetTransversalIsi.justificatif.infrastructure.repository;

import com.projetTransversalIsi.justificatif.infrastructure.entity.JpaJustificatifEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataJustificatifRepository extends JpaRepository<JpaJustificatifEntity, Long> {
    List<JpaJustificatifEntity> findByEtudiantId(Long etudiantId);
}
