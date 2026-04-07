package com.projetTransversalIsi.pedagogie.application.use_cases;

import com.projetTransversalIsi.pedagogie.domain.OffreUeRepository;
import com.projetTransversalIsi.pedagogie.domain.exceptions.OffreUeNotFoundException;
import com.projetTransversalIsi.pedagogie.domain.model.OffreUe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindOffreUeByIdUCImpl implements FindOffreUeByIdUC {

    private final OffreUeRepository offreUeRepository;

    @Override
    public OffreUe execute(Long id) {
        return offreUeRepository.findById(id)
                .orElseThrow(() -> new OffreUeNotFoundException(id));
    }
}
