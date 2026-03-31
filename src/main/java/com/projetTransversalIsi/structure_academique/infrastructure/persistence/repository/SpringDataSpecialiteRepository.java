package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaSpecialiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataSpecialiteRepository extends JpaRepository<JpaSpecialiteEntity, Long> {

    boolean existsByCode(String code);

    Optional<JpaSpecialiteEntity> findByCode(String code);

    List<JpaSpecialiteEntity> findByActiveTrue();

    List<JpaSpecialiteEntity> findByNiveauId(Long niveauId);
}
