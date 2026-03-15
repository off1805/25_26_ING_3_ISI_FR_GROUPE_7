package com.projetTransversalIsi.classe.application.usecase;

import com.projetTransversalIsi.classe.domain.Classe;
import com.projetTransversalIsi.classe.domain.ClasseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindClassesBySpecialiteIdUCImpl implements FindClassesBySpecialiteIdUC {

    private final ClasseRepository repository;

    @Override
    public List<Classe> execute(Long specialiteId) {
        return repository.findBySpecialiteId(specialiteId);
    }
}
