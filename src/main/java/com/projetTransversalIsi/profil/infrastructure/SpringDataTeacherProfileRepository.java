package com.projetTransversalIsi.profil.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTeacherProfileRepository extends JpaRepository<JpaTeacherProfileEntity,Long> {
}
