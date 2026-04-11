package com.projetTransversalIsi.user.profil.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SpringDataStudentProfileRepository extends JpaRepository<JpaStudentProfileEntity, Long>, JpaSpecificationExecutor<JpaStudentProfileEntity> {
    List<JpaStudentProfileEntity> findByClasseId(Long classeId);
}

