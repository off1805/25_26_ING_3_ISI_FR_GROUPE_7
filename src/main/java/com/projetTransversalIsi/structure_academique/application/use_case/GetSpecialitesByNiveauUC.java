package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Specialite;
import com.projetTransversalIsi.structure_academique.domain.repository.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetSpecialitesByNiveauUC {

    private final SpecialiteRepository specialiteRepository;

    public List<Specialite> execute(Long niveauId) {
        return specialiteRepository.findByNiveauId(niveauId);
    }
}
