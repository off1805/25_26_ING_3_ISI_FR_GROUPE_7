package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindFilieresByCycleIdUCImpl implements FindFilieresByCycleIdUC {

    private final FiliereRepository filiereRepository;

    @Override
    public List<Filiere> execute(Long cycleId) {
        return filiereRepository.findByCycleId(cycleId);
    }
}
