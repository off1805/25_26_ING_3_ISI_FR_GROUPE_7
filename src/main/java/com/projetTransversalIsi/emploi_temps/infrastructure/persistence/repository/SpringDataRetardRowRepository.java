package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaRetardRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataRetardRowRepository extends JpaRepository<JpaRetardRowEntity, Long> {
    List<JpaRetardRowEntity> findByRetardListId(Long retardListId);
}
