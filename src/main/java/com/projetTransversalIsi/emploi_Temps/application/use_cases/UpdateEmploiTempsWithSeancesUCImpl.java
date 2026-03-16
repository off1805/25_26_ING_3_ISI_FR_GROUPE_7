package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.UpdateEmploiTempsWithSeancesDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_Temps.domain.exceptions.EmploiTempsNotFoundException;
import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import com.projetTransversalIsi.seance.domain.exceptions.SeanceConflictException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateEmploiTempsWithSeancesUCImpl implements UpdateEmploiTempsWithSeancesUC {

    private final EmploiTempsRepository emploiTempsRepo;
    private final SeanceRepository seanceRepo;

    @Override
    @Transactional
    public EmploiTemps execute(UpdateEmploiTempsWithSeancesDTO command) {

        EmploiTemps emploi = emploiTempsRepo.findById(command.id())
                .orElseThrow(() -> new EmploiTempsNotFoundException(command.id()));

        // Supprimer les anciennes séances liées
        emploi.getSeances().forEach(seanceRepo::delete);
        emploi.getSeances().clear();

        // Mettre à jour les métadonnées de l'emploi
        emploi.update(command.dateDebut(), command.dateFin(), command.semaine(), command.classeId());

        // Ajouter les nouvelles séances
        command.seances().forEach(seanceDTO -> {
            if (seanceRepo.existsConflict(
                    seanceDTO.enseignantId(),
                    seanceDTO.dateSeance(),
                    seanceDTO.heureDebut(),
                    seanceDTO.heureFin())) {
                throw new SeanceConflictException("Conflit d'emploi du temps enseignant");
            }

            Seance seance = new Seance(
                    seanceDTO.libelle(),
                    seanceDTO.salle(),
                    seanceDTO.dateSeance(),
                    seanceDTO.heureDebut(),
                    seanceDTO.heureFin(),
                    seanceDTO.coursId(),
                    seanceDTO.enseignantId()
            );

            emploi.addSeance(seanceRepo.save(seance));
        });

        return emploiTempsRepo.save(emploi);
    }
}
