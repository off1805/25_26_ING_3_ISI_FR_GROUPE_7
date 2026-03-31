package com.projetTransversalIsi.seance.application.services;

import com.projetTransversalIsi.seance.application.dto.*;
import com.projetTransversalIsi.seance.application.use_cases.*;
import com.projetTransversalIsi.seance.domain.Seance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeanceService implements DefaultSeanceService {

    private final CreateSeanceUC createSeanceUC;
    private final UpdateSeanceUC updateSeanceUC;
    private final DeleteSeanceUC deleteSeanceUC;
    private final FindSeanceByIdUC findSeanceByIdUC;
    private final SearchSeanceUC searchSeanceUC;
    private final GetSeancesTodayByEnseignantUC getSeancesTodayByEnseignantUC;

    @Override
    @Transactional
    public SeanceResponseDTO createSeance(CreateSeanceRequestDTO request) {
        Seance seance = createSeanceUC.execute(request);
        return SeanceResponseDTO.fromDomain(seance);
    }

    @Override
    @Transactional
    public SeanceResponseDTO updateSeance(UpdateSeanceRequestDTO request) {
        Seance seance = updateSeanceUC.execute(request);
        return SeanceResponseDTO.fromDomain(seance);
    }

    @Override
    @Transactional
    public void deleteSeance(Long id) {
        deleteSeanceUC.execute(id);
    }

    @Override
    public SeanceResponseDTO getSeanceById(Long id) {
        Seance seance = findSeanceByIdUC.execute(id);
        return SeanceResponseDTO.fromDomain(seance);
    }

    @Override
    public List<SeanceResponseDTO> searchSeances(SearchSeanceRequestDTO criteria) {
        return searchSeanceUC.execute(criteria).stream()
                .map(SeanceResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeanceResponseDTO> getSeancesTodayByEnseignant(Long enseignantId, Boolean includeDeleted) {
        return getSeancesTodayByEnseignantUC.execute(enseignantId, includeDeleted).stream()
                .map(SeanceResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}