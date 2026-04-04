package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.UpdatePresenceRowDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdatePresenceRowUCImpl implements UpdatePresenceRowUC {

    private final PresenceRowRepository presenceRowRepo;

    @Override
    public PresenceRowResponseDTO execute(UpdatePresenceRowDTO dto) {
        PresenceRow row = presenceRowRepo.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("Ligne de présence introuvable : " + dto.id()));
        row.update(dto.present(), dto.heuresAbsence());
        return PresenceRowResponseDTO.fromDomain(presenceRowRepo.save(row));
    }
}
