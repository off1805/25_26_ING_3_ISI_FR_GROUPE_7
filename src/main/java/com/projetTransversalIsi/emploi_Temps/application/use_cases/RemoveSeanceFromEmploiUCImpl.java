package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.AddSeanceToEmploiDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_Temps.domain.exceptions.EmploiTempsNotFoundException;
import com.projetTransversalIsi.emploi_Temps.infrastructure.SpringDataEmploiTempsRepository;
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
    private final SpringDataEmploiTempsRepository springDataEmploiTempsRepo;  // ← AJOUTER CECI
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


        springDataEmploiTempsRepo.deleteSeanceFromEmploi(command.emploiTempsId(), command.seanceId());


        EmploiTemps emploiTempsMaj = emploiTempsRepo.findById(command.emploiTempsId())
                .orElseThrow(() -> new EmploiTempsNotFoundException(command.emploiTempsId()));

        log.info("Taille de la liste après suppression: {}", emploiTempsMaj.getSeances().size());

        return emploiTempsMaj;
    }
}