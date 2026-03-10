package com.projetTransversalIsi.classe.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringdataClasseRepository extends JpaRepository<JpaClasseEntity,Long> {
}
