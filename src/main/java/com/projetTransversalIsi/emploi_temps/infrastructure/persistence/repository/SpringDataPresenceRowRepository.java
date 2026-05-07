package com.projetTransversalIsi.emploi_temps.infrastructure.persistence.repository;

import com.projetTransversalIsi.emploi_temps.infrastructure.persistence.entity.JpaPresenceRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataPresenceRowRepository extends JpaRepository<JpaPresenceRowEntity, Long> {
    List<JpaPresenceRowEntity> findByPresenceListId(Long presenceListId);
    List<JpaPresenceRowEntity> findByEtudiantId(Long etudiantId);
    Optional<JpaPresenceRowEntity> findByPresenceListIdAndEtudiantId(Long presenceListId, Long etudiantId);
}
