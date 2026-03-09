package com.projetTransversalIsi.level.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringdatalevelRepository extends JpaRepository<JPAlevelEntity,Long> {


}
