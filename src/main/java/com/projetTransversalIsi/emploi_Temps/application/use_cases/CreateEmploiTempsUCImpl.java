package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.CreateEmploiTempsRequestDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_Temps.domain.exceptions.EmploiTempsConflictException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateEmploiTempsUCImpl implements CreateEmploiTempsUC {

    private final EmploiTempsRepository emploiTempsRepo;

    @Override
    @Transactional
    public EmploiTemps execute(CreateEmploiTempsRequestDTO command) {

        // Vérifier s'il existe déjà un emploi du temps pour cette période
        if (emploiTempsRepo.existsEmploiForPeriode(
                command.classeId(),
                command.dateDebut(),
                command.dateFin())) {
            throw new EmploiTempsConflictException(
                    "Un emploi du temps existe déjà pour cette période"
            );
        }

        EmploiTemps emploiTemps = new EmploiTemps(
                command.dateDebut(),
                command.dateFin(),
                command.semaine(),
                command.classeId()
        );

        return emploiTempsRepo.save(emploiTemps);
    }
}
