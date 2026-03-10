package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_Temps.domain.exceptions.EmploiTempsNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindEmploiTempsByIdUCImpl implements FindEmploiTempsByIdUC {

    private final EmploiTempsRepository emploiTempsRepo;

    @Override
    public EmploiTemps execute(Long id) {
        return emploiTempsRepo.findById(id)
                .orElseThrow(() -> new EmploiTempsNotFoundException(id));
    }
}