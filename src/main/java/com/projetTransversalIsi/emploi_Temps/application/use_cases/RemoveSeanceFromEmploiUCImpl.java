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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveSeanceFromEmploiUCImpl implements RemoveSeanceFromEmploiUC {

    private final EmploiTempsRepository emploiTempsRepo;
    private final SeanceRepository seanceRepo;

    @Override
    @Transactional
    public EmploiTemps execute(AddSeanceToEmploiDTO command) {

        log.info("Retrait de la séance {} de l'emploi du temps {}",
                command.seanceId(), command.emploiTempsId());


        EmploiTemps emploiTemps = emploiTempsRepo.findById(command.emploiTempsId())
                .orElseThrow(() -> new EmploiTempsNotFoundException(command.emploiTempsId()));


        Seance seance = seanceRepo.findById(command.seanceId())
                .orElseThrow(() -> new SeanceNotFoundException(command.seanceId()));


        emploiTemps.removeSeance(seance);
        EmploiTemps saved = emploiTempsRepo.save(emploiTemps);
        log.info("Taille de la liste après suppression: {}", saved.getSeances().size());
        return saved;
    }
}
