package com.projetTransversalIsi.school.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SpringDataSchoolRepository extends JpaRepository<JpaSchoolEntity,Long> {
}
