package com.projetTransversalIsi.cours.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCoursRepository extends JpaRepository<JpaCoursEntity,Long> {
}
