/*package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkRetardUCImpl implements MarkRetardUC {

    private final PresenceRowRepository presenceRowRepo;

    @Override
    public PresenceRowResponseDTO execute(Long presenceRowId, boolean retard) {
        PresenceRow row = presenceRowRepo.findById(presenceRowId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de presence introuvable : " + presenceRowId));
        // markRetard() gere uniquement le champ retard (et force present=true si retard=true).
        // Il ne touche pas present si retard=false, preservant ainsi les absences existantes.
        row.markRetard(retard);
        return PresenceRowResponseDTO.fromDomain(presenceRowRepo.save(row));
    }
}*/
