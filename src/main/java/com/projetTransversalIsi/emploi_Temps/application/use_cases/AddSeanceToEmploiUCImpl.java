package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.AddSeanceToEmploiDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_Temps.domain.exceptions.EmploiTempsNotFoundException;
import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import com.projetTransversalIsi.seance.domain.exceptions.SeanceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddSeanceToEmploiUCImpl implements AddSeanceToEmploiUC {

    private final EmploiTempsRepository emploiTempsRepo;
    private final SeanceRepository seanceRepo;

    @Override
    @Transactional
    public EmploiTemps execute(AddSeanceToEmploiDTO command) {

        EmploiTemps emploiTemps = emploiTempsRepo.findById(command.emploiTempsId())
                .orElseThrow(() -> new EmploiTempsNotFoundException(command.emploiTempsId()));

        Seance seance = seanceRepo.findById(command.seanceId())
                .orElseThrow(() -> new SeanceNotFoundException(command.seanceId()));

        emploiTemps.addSeance(seance);

        return emploiTempsRepo.save(emploiTemps);
    }
}