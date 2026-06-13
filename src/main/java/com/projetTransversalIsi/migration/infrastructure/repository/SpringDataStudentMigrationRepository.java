package com.projetTransversalIsi.migration.infrastructure.repository;

import com.projetTransversalIsi.migration.infrastructure.entity.JpaStudentMigrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataStudentMigrationRepository extends JpaRepository<JpaStudentMigrationEntity, Long> {
    List<JpaStudentMigrationEntity> findByExecutedFalse();
    List<JpaStudentMigrationEntity> findByExecutedFalseAndClasseSource_Specialite_Niveau_Filiere_Id(Long filiereId);
    Optional<JpaStudentMigrationEntity> findByStudent_IdAndExecutedFalse(Long studentId);
}
