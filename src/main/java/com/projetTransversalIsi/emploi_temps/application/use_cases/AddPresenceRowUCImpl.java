package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.CreatePresenceRowDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddPresenceRowUCImpl implements AddPresenceRowUC {

    private final PresenceRowRepository presenceRowRepo;

    @Override
    public PresenceRowResponseDTO execute(CreatePresenceRowDTO dto) {
        PresenceRow row = new PresenceRow(dto.presenceListId(), dto.etudiantId(), dto.present());
        return PresenceRowResponseDTO.fromDomain(presenceRowRepo.save(row));
    }
}
