package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaInfoRetardRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataInfoRetardRowRepository extends JpaRepository<JpaInfoRetardRowEntity, Long> {
    List<JpaInfoRetardRowEntity> findByRetardRowId(Long retardRowId);
    List<JpaInfoRetardRowEntity> findByRetardRowIdIn(List<Long> retardRowIds);
}
