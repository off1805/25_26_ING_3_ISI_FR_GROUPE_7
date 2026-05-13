package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.domain.repository.AppelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteAppelUCImpl implements DeleteAppelUC {

    private final AppelRepository appelRepo;

    @Override
    public void execute(Long id) {
        appelRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appel introuvable : " + id));
        appelRepo.delete(id);
    }
}
