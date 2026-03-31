package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Specialite;
import com.projetTransversalIsi.structure_academique.domain.repository.SpecialiteRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.SpecialiteNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToggleSpecialiteStatusUC {

    private final SpecialiteRepository specialiteRepository;

    @Transactional
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
