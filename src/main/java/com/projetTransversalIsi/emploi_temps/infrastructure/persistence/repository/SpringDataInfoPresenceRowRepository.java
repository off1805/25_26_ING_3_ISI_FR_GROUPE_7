package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaInfoPresenceRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpringDataInfoPresenceRowRepository extends JpaRepository<JpaInfoPresenceRowEntity, Long> {

    @Query("SELECT DISTINCT r.appelId FROM JpaInfoPresenceRowEntity r WHERE r.isPresent IS NULL")
    List<Long> findDistinctAppelIdsWithPendingRows();
    List<JpaInfoPresenceRowEntity> findByPresenceRowId(Long presenceRowId);
    List<JpaInfoPresenceRowEntity> findByPresenceRowIdIn(java.util.List<Long> presenceRowIds);
    List<JpaInfoPresenceRowEntity> findByAppelId(Long appelId);
    List<JpaInfoPresenceRowEntity> findByAppelIdAndIsPresentIsNull(Long appelId);
}
