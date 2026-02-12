package com.projetTransversalIsi.school.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSchoolRepository extends JpaRepository<JpaSchoolEntity,Long> {
}
