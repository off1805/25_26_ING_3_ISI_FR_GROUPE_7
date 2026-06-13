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
    List<JpaClasseEntity> findBySpecialite_Niveau_Filiere_Id(Long filiereId);
    List<JpaClasseEntity> findBySpecialite_Niveau_Filiere_Cycle_Id(Long cycleId);
    List<JpaClasseEntity> findBySpecialite_Niveau_Filiere_IdAndSpecialite_Niveau_Ordre(Long filiereId, int ordre);
    List<JpaClasseEntity> findBySpecialite_Niveau_Filiere_Cycle_IdAndSpecialite_Niveau_Ordre(Long cycleId, int ordre);
}
