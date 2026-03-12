package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.domain.Classe;
import com.projetTransversalIsi.classe.domain.ClasseRepository;
import com.projetTransversalIsi.classe.domain.exceptions.ClasseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindClasseByIdUCImpl implements FindClasseByIdUC {
    private final ClasseRepository repository;

    @Override
    public Classe execute(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ClasseNotFoundException(id));
    }
}
