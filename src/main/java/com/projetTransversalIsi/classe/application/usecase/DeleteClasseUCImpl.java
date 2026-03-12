package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.domain.ClasseRepository;
import com.projetTransversalIsi.classe.domain.exceptions.ClasseNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteClasseUCImpl implements DeleteClasseUC {
    private final ClasseRepository repository;

    @Transactional
    @Override
    public void execute(Long id) {
        if (!repository.findById(id).isPresent()) {
            throw new ClasseNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
