package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaAttendanceCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataAttendanceCodeRepository extends JpaRepository<JpaAttendanceCodeEntity, Long> {
    List<JpaAttendanceCodeEntity> findBySeanceId(Long seanceId);
    List<JpaAttendanceCodeEntity> findByEnseignantId(Long enseignantId);
}
