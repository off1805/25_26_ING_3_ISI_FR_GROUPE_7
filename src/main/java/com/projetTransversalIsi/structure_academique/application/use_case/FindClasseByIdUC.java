package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.model.Classe;
import com.projetTransversalIsi.structure_academique.domain.repository.ClasseRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.ClasseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindClasseByIdUC {
    private final ClasseRepository repository;

    public Classe execute(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ClasseNotFoundException(id));
    }
}
