package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.domain.repository.NiveauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindNiveauxByFiliereIdUC {

    private final NiveauRepository repository;

    public List<Niveau> execute(Long filiereId) {
        return repository.findByFiliereId(filiereId);
    }
}
