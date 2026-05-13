package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaAppelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataAppelRepository extends JpaRepository<JpaAppelEntity, Long> {
    List<JpaAppelEntity> findByPresenceListId(Long presenceListId);
    Optional<JpaAppelEntity> findByAttendanceCodeId(Long attendanceCodeId);
}
