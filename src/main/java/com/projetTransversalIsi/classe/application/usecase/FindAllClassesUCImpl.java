package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.domain.Classe;
import com.projetTransversalIsi.classe.domain.ClasseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllClassesUCImpl implements FindAllClassesUC {
    private final ClasseRepository repository;

    @Override
    public List<Classe> execute() {
        return repository.findAll();
    }
}
