package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;

import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaOffreUeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOffreUeRepository extends JpaRepository<JpaOffreUeEntity, Long> {

    boolean existsByUe_IdAndAnneeScolaire_Id(Long ueId, Long anneeScolaireId);

    Page<JpaOffreUeEntity> findByAnneeScolaire_Id(Long anneeScolaireId, Pageable pageable);

    Page<JpaOffreUeEntity> findByUe_Id(Long ueId, Pageable pageable);
}
