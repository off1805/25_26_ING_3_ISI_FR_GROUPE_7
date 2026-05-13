package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.InfoPresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceListResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.model.PresenceRow;
import com.projetTransversalIsi.emploi_temps.domain.repository.InfoPresenceRowRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceListRepository;
import com.projetTransversalIsi.emploi_temps.domain.repository.PresenceRowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetPresenceListUCImpl implements GetPresenceListUC {

    private final PresenceListRepository presenceListRepo;
    private final PresenceRowRepository presenceRowRepo;
    private final InfoPresenceRowRepository infoPresenceRowRepo;

    @Override
    public PresenceListResponseDTO getById(Long id) {
        return presenceListRepo.findById(id)
                .map(PresenceListResponseDTO::fromDomain)
                .orElseThrow(() -> new IllegalArgumentException("Liste de présence introuvable : " + id));
    }

    @Override
    public List<PresenceRowResponseDTO> getRows(Long presenceListId) {
        return presenceRowRepo.findByPresenceListId(presenceListId)
                .stream()
                .map(PresenceRowResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<InfoPresenceRowResponseDTO> getInfoRows(Long presenceListId) {
        List<Long> presenceRowIds = presenceRowRepo.findByPresenceListId(presenceListId)
                .stream().map(PresenceRow::getId).collect(Collectors.toList());
        if (presenceRowIds.isEmpty()) return List.of();
        return infoPresenceRowRepo.findByPresenceRowIdIn(presenceRowIds)
                .stream()
                .map(InfoPresenceRowResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}
