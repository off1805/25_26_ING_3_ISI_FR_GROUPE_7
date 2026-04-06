package com.projetTransversalIsi.pedagogie.domain;

import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OffreUeRepository {

    boolean offreUeAlreadyExists(Long ueId, Long anneeScolaireId);

    OffreUe save(OffreUe offreUe);

    Optional<OffreUe> findById(Long id);

    void delete(OffreUe offreUe);

    Page<OffreUe> findAll(Pageable pageable);

    Page<OffreUe> findByAnneeScolaireId(Long anneeScolaireId, Pageable pageable);

    Page<OffreUe> findByUeId(Long ueId, Pageable pageable);
}
