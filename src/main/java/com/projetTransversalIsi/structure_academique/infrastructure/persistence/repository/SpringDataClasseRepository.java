package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaClasseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataClasseRepository extends JpaRepository<JpaClasseEntity, Long> {
    Optional<JpaClasseEntity> findByCode(String code);
    List<JpaClasseEntity> findBySpecialiteId(Long specialiteId);
    boolean existsByCode(String code);
}
