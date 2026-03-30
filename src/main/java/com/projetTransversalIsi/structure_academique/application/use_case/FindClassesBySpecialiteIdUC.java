package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Classe;
import com.projetTransversalIsi.structure_academique.domain.repository.ClasseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindClassesBySpecialiteIdUC {

    private final ClasseRepository repository;

    public List<Classe> execute(Long specialiteId) {
        return repository.findBySpecialiteId(specialiteId);
    }
}
