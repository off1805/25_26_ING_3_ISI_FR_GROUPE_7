package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.InfoRetardRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.InfoRetardRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoRetardRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToggleInfoRetardRowUCImpl implements ToggleInfoRetardRowUC {

    private final InfoRetardRowRepository infoRetardRowRepo;

    @Override
    public InfoRetardRowResponseDTO execute(Long infoRetardRowId) {
        InfoRetardRow row = infoRetardRowRepo.findById(infoRetardRowId)
                .orElseThrow(() -> new IllegalArgumentException("Cellule de retard introuvable : " + infoRetardRowId));
        row.toggle();
        return InfoRetardRowResponseDTO.fromDomain(infoRetardRowRepo.save(row));
    }
}
