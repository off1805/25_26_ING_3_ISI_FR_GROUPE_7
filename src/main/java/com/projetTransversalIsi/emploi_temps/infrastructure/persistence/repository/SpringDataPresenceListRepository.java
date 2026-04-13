package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaPresenceListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataPresenceListRepository extends JpaRepository<JpaPresenceListEntity, Long> {
    List<JpaPresenceListEntity> findBySeanceId(Long seanceId);
    List<JpaPresenceListEntity> findByClasseId(Long classeId);
    List<JpaPresenceListEntity> findByDeletedFalse();
}
