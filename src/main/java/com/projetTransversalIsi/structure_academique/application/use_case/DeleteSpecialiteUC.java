package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.repository.SpecialiteRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.SpecialiteNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSpecialiteUC {

    private final SpecialiteRepository specialiteRepository;

    @Transactional
    public void execute(Long id) {
        if (!specialiteRepository.findById(id).isPresent()) {
            throw new SpecialiteNotFoundException(id);
        }
        specialiteRepository.deleteById(id);
    }
}
