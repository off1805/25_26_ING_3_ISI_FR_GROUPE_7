package com.projetTransversalIsi.structure_academique.application.service;

import com.projetTransversalIsi.structure_academique.application.dto.*;
import com.projetTransversalIsi.structure_academique.application.use_case.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialiteService {

    private final CreateSpecialiteUC createSpecialiteUC;
    private final UpdateSpecialiteUC updateSpecialiteUC;
    private final DeleteSpecialiteUC deleteSpecialiteUC;
    private final FindSpecialiteByIdUC findSpecialiteByIdUC;
    private final GetAllSpecialitesUC getAllSpecialitesUC;
    private final GetSpecialitesByNiveauUC getSpecialitesByNiveauUC;
    private final ToggleSpecialiteStatusUC toggleSpecialiteStatusUC;

    public SpecialiteResponseDTO createSpecialite(CreateSpecialiteRequestDTO request) {
        return SpecialiteResponseDTO.fromDomain(createSpecialiteUC.execute(request));
    }

    public SpecialiteResponseDTO updateSpecialite(Long id, UpdateSpecialiteRequestDTO request) {
        return SpecialiteResponseDTO.fromDomain(updateSpecialiteUC.execute(id, request));
    }

    public void deleteSpecialite(Long id) {
        deleteSpecialiteUC.execute(id);
    }

    public SpecialiteResponseDTO getSpecialiteById(Long id) {
        return SpecialiteResponseDTO.fromDomain(findSpecialiteByIdUC.execute(id));
    }

    public List<SpecialiteResponseDTO> getAllSpecialites() {
        return getAllSpecialitesUC.execute().stream()
                .map(SpecialiteResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    public List<SpecialiteResponseDTO> getSpecialitesByNiveauId(Long niveauId) {
        return getSpecialitesByNiveauUC.execute(niveauId).stream()
                .map(SpecialiteResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    public void toggleStatus(Long id, boolean activer) {
        toggleSpecialiteStatusUC.execute(id, activer);
    }
}
