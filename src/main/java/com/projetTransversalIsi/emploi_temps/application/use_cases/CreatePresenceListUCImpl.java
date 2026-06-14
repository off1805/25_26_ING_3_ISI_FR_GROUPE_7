package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.CreatePresenceListDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceListResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository;
import com.projetTransversalIsi.pedagogie.domain.AnneeScolaireRepository;
import com.projetTransversalIsi.pedagogie.domain.model.AnneeScolaire;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatePresenceListUCImpl implements CreatePresenceListUC {

    private final PresenceListRepository presenceListRepo;
    private final AnneeScolaireRepository anneeScolaireRepository;

    // L'enseignant ouvre manuellement la feuille avant de lancer le scan.
    // createdAt est fixé dans le constructeur PresenceList ; pas de vérification de doublon
    // (une séance peut avoir plusieurs feuilles si l'enseignant recrée — MarkStudentPresentUC prend la première).
    @Override
    public PresenceListResponseDTO execute(CreatePresenceListDTO dto) {
        PresenceList presenceList = new PresenceList(
                dto.seanceId(), dto.classeId(), dto.ueId(), dto.enseignantId(), dto.date()
        );
        anneeScolaireRepository.findActive().map(AnneeScolaire::getId)
                .ifPresent(presenceList::setAnneeScolaireId);
        return PresenceListResponseDTO.fromDomain(presenceListRepo.save(presenceList));
    }
}
