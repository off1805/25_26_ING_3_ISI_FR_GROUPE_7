package com.projetTransversalIsi.Niveau.application.use_cases;

import com.projetTransversalIsi.Niveau.domain.Niveau;
import com.projetTransversalIsi.Niveau.domain.NiveauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindNiveauxByFiliereIdUCImpl implements FindNiveauxByFiliereIdUC {

    private final NiveauRepository repository;

    @Override
    public List<Niveau> execute(Long filiereId) {
        return repository.findByFiliereId(filiereId);
    }
}
