package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.domain.repository.ClasseRepository;
import com.projetTransversalIsi.structure_academique.domain.exception.ClasseNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteClasseUC {
    private final ClasseRepository repository;

    @Transactional
    public void execute(Long id) {
        if (!repository.findById(id).isPresent()) {
            throw new ClasseNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
