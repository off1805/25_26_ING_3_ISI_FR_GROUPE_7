package com.projetTransversalIsi.Filiere.application.services;

import com.projetTransversalIsi.Filiere.application.dto.*;
import com.projetTransversalIsi.Filiere.application.use_cases.*;
import com.projetTransversalIsi.Filiere.domain.Filiere;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FiliereService implements DefaultFiliereService {

    private final CreateFiliereUC createFiliereUC;
    private final UpdateFiliereUC updateFiliereUC;
    private final DeleteFiliereUC deleteFiliereUC;
    private final FindFiliereByIdUC findFiliereByIdUC;
    private final SearchFiliereUC searchFiliereUC;

    @Override
    @Transactional
    public FiliereResponseDTO createFiliere(CreateFiliereRequestDTO request) {
        Filiere filiere = createFiliereUC.execute(request);
        return FiliereResponseDTO.fromDomain(filiere);
    }

    @Override
    @Transactional
    public FiliereResponseDTO updateFiliere(UpdateFiliereRequestDTO request) {
        Filiere filiere = updateFiliereUC.execute(request);
        return FiliereResponseDTO.fromDomain(filiere);
    }

    @Override
    @Transactional
    public void deleteFiliere(Long id) {
        deleteFiliereUC.execute(id);
    }

    @Override
    public FiliereResponseDTO getFiliereById(Long id) {
        Filiere filiere = findFiliereByIdUC.execute(id);
        return FiliereResponseDTO.fromDomain(filiere);
    }

    @Override
    public List<FiliereResponseDTO> searchFilieres(SearchFiliereRequestDTO criteria) {
        return searchFiliereUC.execute(criteria).stream()
                .map(FiliereResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}