package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaRetardListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataRetardListRepository extends JpaRepository<JpaRetardListEntity, Long> {
    Optional<JpaRetardListEntity> findByClasseIdAndSemaineDebutAndDeletedFalse(Long classeId, LocalDate semaineDebut);
    List<JpaRetardListEntity> findByClasseId(Long classeId);
}
