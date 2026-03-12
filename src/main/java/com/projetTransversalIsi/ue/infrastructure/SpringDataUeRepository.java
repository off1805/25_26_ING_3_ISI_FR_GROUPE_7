package com.projetTransversalIsi.ue.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataUeRepository
        extends JpaRepository<JpaUeEntity, Long>, JpaSpecificationExecutor<JpaUeEntity> {
    boolean existsByCode(String code);
}
