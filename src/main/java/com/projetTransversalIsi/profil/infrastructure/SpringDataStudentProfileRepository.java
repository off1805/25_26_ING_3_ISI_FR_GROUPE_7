package com.projetTransversalIsi.profil.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataStudentProfileRepository extends JpaRepository<JpaStudentProfileEntity,Long> {
}
