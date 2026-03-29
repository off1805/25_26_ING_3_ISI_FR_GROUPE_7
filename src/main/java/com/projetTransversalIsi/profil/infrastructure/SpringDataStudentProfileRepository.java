package com.projetTransversalIsi.profil.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataStudentProfileRepository extends JpaRepository<JpaStudentProfileEntity, Long> {
    List<JpaStudentProfileEntity> findByClasseId(Long classeId);
}

