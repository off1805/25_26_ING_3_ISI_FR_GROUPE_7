package com.projetTransversalIsi.classe.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringdataClasseRepository extends JpaRepository<JpaClasseEntity, Long> {
    Optional<JpaClasseEntity> findByCode(String code);
    List<JpaClasseEntity> findBySpecialiteId(Long specialiteId);
    boolean existsByCode(String code);
}
