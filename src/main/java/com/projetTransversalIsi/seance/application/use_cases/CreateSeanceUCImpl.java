package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.application.dto.CreateSeanceRequestDTO;
import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import com.projetTransversalIsi.seance.domain.exceptions.SeanceConflictException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateSeanceUCImpl implements CreateSeanceUC {

    private final SeanceRepository seanceRepo;

    @Override
    @Transactional
    public Seance execute(CreateSeanceRequestDTO command) {


        if (seanceRepo.existsConflict(
                command.enseignantId(),
                command.dateSeance(),
                command.heureDebut(),
                command.heureFin())) {
            throw new SeanceConflictException(
                    "L'enseignant a déjà une séance programmée à cette période"
            );
        }


        if (seanceRepo.existsConflictForSalle(
                command.salle(),
                command.dateSeance(),
                command.heureDebut(),
                command.heureFin())) {
            throw new SeanceConflictException(
                    "La salle est déjà occupée à cette période"
            );
        }

        Seance seance = new Seance(
                command.libelle(),
                command.dateSeance(),
                command.heureDebut(),
                command.heureFin(),
                command.coursId(),
                command.enseignantId(),
                command.salle()
        );

        return seanceRepo.save(seance);
    }
}