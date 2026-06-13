package com.projetTransversalIsi.migration.infrastructure.repository;

import com.projetTransversalIsi.migration.infrastructure.entity.JpaStudentExpulsionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataStudentExpulsionRepository extends JpaRepository<JpaStudentExpulsionEntity, Long> {
}
