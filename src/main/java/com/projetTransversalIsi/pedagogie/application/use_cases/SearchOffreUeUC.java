package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchOffreUeUC {
    Page<OffreUe> execute(Long anneeScolaireId, Long ueId, Pageable pageable);
}
