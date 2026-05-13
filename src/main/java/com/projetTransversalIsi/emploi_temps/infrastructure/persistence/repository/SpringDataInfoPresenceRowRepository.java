package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaInfoPresenceRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataInfoPresenceRowRepository extends JpaRepository<JpaInfoPresenceRowEntity, Long> {
    List<JpaInfoPresenceRowEntity> findByPresenceRowId(Long presenceRowId);
    List<JpaInfoPresenceRowEntity> findByPresenceRowIdIn(java.util.List<Long> presenceRowIds);
    List<JpaInfoPresenceRowEntity> findByAppelId(Long appelId);
    java.util.Optional<JpaInfoPresenceRowEntity> findByAppelIdAndEtudiantId(Long appelId, Long etudiantId);
    List<JpaInfoPresenceRowEntity> findByAppelIdAndIsPresentIsNull(Long appelId);
}
