package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.CreatePresenceListDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceListResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceList;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatePresenceListUCImpl implements CreatePresenceListUC {

    private final PresenceListRepository presenceListRepo;

    @Override
    public PresenceListResponseDTO execute(CreatePresenceListDTO dto) {
        PresenceList presenceList = new PresenceList(
                dto.seanceId(), dto.classeId(), dto.ueId(), dto.enseignantId(), dto.date()
        );
        return PresenceListResponseDTO.fromDomain(presenceListRepo.save(presenceList));
    }
}
