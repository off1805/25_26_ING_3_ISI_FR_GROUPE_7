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


        String libelle = (command.libelle() == null || command.libelle().isBlank())
                ? "Séance"
                : command.libelle();

        String salle = (command.salle() == null || command.salle().isBlank())
                ? "Salle 1"
                : command.salle();

        Seance seance = new Seance(
                libelle,
                salle,
                command.dateSeance(),
                command.heureDebut(),
                command.heureFin(),
                command.coursId(),
                command.enseignantId()
        );

        return seanceRepo.save(seance);
    }
}
