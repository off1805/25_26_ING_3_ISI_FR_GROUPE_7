package com.projetTransversalIsi.structure_academique.infrastructure.persistence.repository;

import com.projetTransversalIsi.structure_academique.domain.model.CycleStatus;
import com.projetTransversalIsi.structure_academique.infrastructure.persistence.entity.JpaCycleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataCycleRepository extends JpaRepository<JpaCycleEntity, Long> {

    boolean existsByCode(String code);

    Optional<JpaCycleEntity> findByCode(String code);

    List<JpaCycleEntity> findByDeletedFalse();

    List<JpaCycleEntity> findByDeletedTrue();

    List<JpaCycleEntity> findByStatusAndDeletedFalse(CycleStatus status);

    @Query("SELECT c FROM JpaCycleEntity c WHERE c.deleted = true AND c.deletedAt >= :since")
    List<JpaCycleEntity> findDeletedSince(@Param("since") LocalDateTime since);
}
