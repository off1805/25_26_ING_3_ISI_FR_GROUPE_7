package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_Temps.domain.exceptions.EmploiTempsNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteEmploiTempsUCImpl implements DeleteEmploiTempsUC {

    private final EmploiTempsRepository emploiTempsRepo;

    @Override
    @Transactional
    public void execute(Long id) {

        EmploiTemps emploiTemps = emploiTempsRepo.findById(id)
                .orElseThrow(() -> new EmploiTempsNotFoundException(id));

        if (emploiTemps.isDeleted()) {
            throw new IllegalStateException("L'emploi du temps " + id + " est déjà supprimé");
        }

        emploiTemps.delete();
        emploiTempsRepo.save(emploiTemps);
    }
}