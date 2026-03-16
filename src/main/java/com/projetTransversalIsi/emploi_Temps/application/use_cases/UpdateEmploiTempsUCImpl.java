package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.UpdateEmploiTempsRequestDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_Temps.domain.exceptions.EmploiTempsNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateEmploiTempsUCImpl implements UpdateEmploiTempsUC {

    private final EmploiTempsRepository emploiTempsRepo;

    @Override
    @Transactional
    public EmploiTemps execute(UpdateEmploiTempsRequestDTO command) {

        EmploiTemps emploiTemps = emploiTempsRepo.findById(command.id())
                .orElseThrow(() -> new EmploiTempsNotFoundException(command.id()));

        emploiTemps.update(
                command.dateDebut(),
                command.dateFin(),
                command.semaine(),
                command.classeId()
        );

        return emploiTempsRepo.save(emploiTemps);
    }
}
