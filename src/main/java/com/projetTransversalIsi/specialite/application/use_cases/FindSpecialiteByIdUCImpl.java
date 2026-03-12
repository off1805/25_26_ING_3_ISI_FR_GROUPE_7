package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import com.projetTransversalIsi.specialite.domain.exceptions.SpecialiteNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindSpecialiteByIdUCImpl implements FindSpecialiteByIdUC {

    private final SpecialiteRepository specialiteRepository;

    @Override
    public Specialite execute(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new SpecialiteNotFoundException(id));
    }
}
