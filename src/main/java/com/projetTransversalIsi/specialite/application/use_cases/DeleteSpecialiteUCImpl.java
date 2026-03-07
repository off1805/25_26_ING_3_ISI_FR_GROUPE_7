package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import com.projetTransversalIsi.specialite.domain.exceptions.SpecialiteNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSpecialiteUCImpl implements DeleteSpecialiteUC {

    private final SpecialiteRepository specialiteRepository;

    @Transactional
    @Override
    public void execute(Long id) {
        if (!specialiteRepository.findById(id).isPresent()) {
            throw new SpecialiteNotFoundException(id);
        }
        specialiteRepository.deleteById(id);
    }
}
