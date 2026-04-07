package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchOffreUeUCImpl implements SearchOffreUeUC {

    private final OffreUeRepository offreUeRepository;

    @Override
    public Page<OffreUe> execute(Long anneeScolaireId, Long ueId, Pageable pageable) {
        if (anneeScolaireId != null) {
            return offreUeRepository.findByAnneeScolaireId(anneeScolaireId, pageable);
        }
        if (ueId != null) {
            return offreUeRepository.findByUeId(ueId, pageable);
        }
        return offreUeRepository.findAll(pageable);
    }
}
