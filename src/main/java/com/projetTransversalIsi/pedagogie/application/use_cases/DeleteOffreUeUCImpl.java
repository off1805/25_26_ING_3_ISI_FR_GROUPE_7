package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.exceptions.OffreUeNotFoundException;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteOffreUeUCImpl implements DeleteOffreUeUC {

    private final OffreUeRepository offreUeRepository;

    @Transactional
    @Override
    public void execute(Long id) {
        OffreUe offreUe = offreUeRepository.findById(id)
                .orElseThrow(() -> new OffreUeNotFoundException(id));
        offreUeRepository.delete(offreUe);
    }
}
