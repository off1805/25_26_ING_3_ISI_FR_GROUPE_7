package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.PresenceListResponseDTO;
import com.projetTransversalIsi.emploi_temps.application.dto.PresenceRowResponseDTO;
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
}
