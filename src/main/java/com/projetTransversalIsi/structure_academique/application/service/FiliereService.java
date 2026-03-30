package com.projetTransversalIsi.structure_academique.application.service;

import com.projetTransversalIsi.structure_academique.application.dto.*;
import com.projetTransversalIsi.structure_academique.application.use_case.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FiliereService {

    private final CreateFiliereUC createFiliereUC;
    private final UpdateFiliereUC updateFiliereUC;
    private final DeleteFiliereUC deleteFiliereUC;
    private final FindFiliereByIdUC findFiliereByIdUC;
    private final FindFilieresByCycleIdUC findFilieresByCycleIdUC;
    private final SearchFiliereUC searchFiliereUC;

    public FiliereResponseDTO createFiliere(CreateFiliereRequestDTO request) {
        return FiliereResponseDTO.fromDomain(createFiliereUC.execute(request));
    }

    public FiliereResponseDTO updateFiliere(Long id, UpdateFiliereRequestDTO request) {
        return FiliereResponseDTO.fromDomain(updateFiliereUC.execute(request, id));
    }

    public void deleteFiliere(Long id) {
        deleteFiliereUC.execute(id);
    }

    public FiliereResponseDTO getFiliereById(Long id) {
        return FiliereResponseDTO.fromDomain(findFiliereByIdUC.execute(id));
    }

    public List<FiliereResponseDTO> getFilieresByCycleId(Long cycleId) {
        return findFilieresByCycleIdUC.execute(cycleId).stream()
                .map(FiliereResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    public List<FiliereResponseDTO> searchFilieres(SearchFiliereRequestDTO criteria) {
        return searchFiliereUC.execute(criteria).stream()
                .map(FiliereResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}
