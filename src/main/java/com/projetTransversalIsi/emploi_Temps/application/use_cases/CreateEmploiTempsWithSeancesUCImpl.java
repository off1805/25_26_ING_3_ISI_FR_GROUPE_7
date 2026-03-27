package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.CreateEmploiTempsWithSeancesDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTempsRepository;
import com.projetTransversalIsi.emploi_Temps.domain.exceptions.EmploiTempsConflictException;
import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import com.projetTransversalIsi.seance.domain.exceptions.SeanceConflictException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateEmploiTempsWithSeancesUCImpl implements CreateEmploiTempsWithSeancesUC {

    private final EmploiTempsRepository emploiTempsRepo;
    private final SeanceRepository seanceRepo;

    @Override
    @Transactional
    public EmploiTemps execute(CreateEmploiTempsWithSeancesDTO command) {

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

        command.seances().forEach(seanceDTO -> {
            Seance.TypeSeance type = seanceDTO.resolvedType();

            // Vérifier les conflits enseignant uniquement pour les séances de cours
            if (Seance.TypeSeance.SEANCE.equals(type) && seanceDTO.enseignantId() != null) {
                if (seanceRepo.existsConflict(
                        seanceDTO.enseignantId(),
                        seanceDTO.dateSeance(),
                        seanceDTO.heureDebut(),
                        seanceDTO.heureFin())) {
                    throw new SeanceConflictException(
                            "L'enseignant a déjà une séance programmée à cette période"
                    );
                }
            }

            Seance seance;
            if (Seance.TypeSeance.EVENEMENT.equals(type)) {
                // Créer un événement (sans cours ni enseignant)
                seance = new Seance(
                        seanceDTO.libelle(),
                        seanceDTO.dateSeance(),
                        seanceDTO.heureDebut(),
                        seanceDTO.heureFin(),
                        seanceDTO.couleur(),
                        seanceDTO.iconKey()
                );
            } else {
                // Créer une séance de cours normale
                String libelle = (seanceDTO.libelle() == null || seanceDTO.libelle().isBlank())
                        ? "Séance"
                        : seanceDTO.libelle();

                String salle = (seanceDTO.salle() == null || seanceDTO.salle().isBlank())
                        ? "Salle 1"
                        : seanceDTO.salle();

                seance = new Seance(
                        libelle,
                        salle,
                        seanceDTO.dateSeance(),
                        seanceDTO.heureDebut(),
                        seanceDTO.heureFin(),
                        seanceDTO.coursId(),
                        seanceDTO.enseignantId()
                );
                seance.setCouleur(seanceDTO.couleur());
            }

            emploiTemps.addSeance(seanceRepo.save(seance));
        });

        return emploiTempsRepo.save(emploiTemps);
    }
}
