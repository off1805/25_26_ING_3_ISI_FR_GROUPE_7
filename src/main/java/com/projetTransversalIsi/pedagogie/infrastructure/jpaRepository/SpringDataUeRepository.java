package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;

import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaUeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataUeRepository
        extends JpaRepository<JpaUeEntity, Long>, JpaSpecificationExecutor<JpaUeEntity> {
    boolean existsByCode(String code);
}
