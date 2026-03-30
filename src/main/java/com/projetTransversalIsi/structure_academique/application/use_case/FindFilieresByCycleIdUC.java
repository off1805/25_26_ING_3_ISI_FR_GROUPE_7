package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import com.projetTransversalIsi.structure_academique.domain.repository.FiliereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindFilieresByCycleIdUC {

    private final FiliereRepository filiereRepository;

    public List<Filiere> execute(Long cycleId) {
        return filiereRepository.findByCycleId(cycleId);
    }
}
