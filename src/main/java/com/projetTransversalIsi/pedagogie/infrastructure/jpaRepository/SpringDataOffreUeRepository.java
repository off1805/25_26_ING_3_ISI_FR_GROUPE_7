package com.projetTransversalIsi.pedagogie.infrastructure.jpaRepository;

import com.projetTransversalIsi.pedagogie.infrastructure.entity.JpaOffreUeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SpringDataOffreUeRepository extends JpaRepository<JpaOffreUeEntity, Long>, JpaSpecificationExecutor<JpaOffreUeEntity> {

    boolean existsByUe_IdAndAnneeScolaire_Id(Long ueId, Long anneeScolaireId);

    Page<JpaOffreUeEntity> findByAnneeScolaire_Id(Long anneeScolaireId, Pageable pageable);

    Page<JpaOffreUeEntity> findByUe_Id(Long ueId, Pageable pageable);

    Page<JpaOffreUeEntity> findBySpecialiteIdAndAnneeScolaire_Id(Long specialiteId, Long anneeScolaireId, Pageable pageable);

    Optional<JpaOffreUeEntity> findByUe_IdAndAnneeScolaire_Id(Long ueId, Long anneeScolaireId);
}
