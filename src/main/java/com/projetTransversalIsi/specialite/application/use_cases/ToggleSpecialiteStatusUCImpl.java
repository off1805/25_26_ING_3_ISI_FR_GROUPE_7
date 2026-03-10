package com.projetTransversalIsi.specialite.application.use_cases;

import com.projetTransversalIsi.specialite.domain.Specialite;
import com.projetTransversalIsi.specialite.domain.SpecialiteRepository;
import com.projetTransversalIsi.specialite.domain.exceptions.SpecialiteNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToggleSpecialiteStatusUCImpl implements ToggleSpecialiteStatusUC {

    private final SpecialiteRepository specialiteRepository;

    @Transactional
    @Override
    public Specialite execute(Long id, boolean activer) {
        Specialite specialite = specialiteRepository.findById(id)
                .orElseThrow(() -> new SpecialiteNotFoundException(id));

        if (activer) {
            specialite.activer();
        } else {
            specialite.desactiver();
        }

        return specialiteRepository.save(specialite);
    }
}
