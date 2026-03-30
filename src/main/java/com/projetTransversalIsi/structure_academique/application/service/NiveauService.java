package com.projetTransversalIsi.structure_academique.application.service;

import com.projetTransversalIsi.structure_academique.application.dto.*;
import com.projetTransversalIsi.structure_academique.application.use_case.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NiveauService {

    private final CreateNiveauUC createNiveauUC;
    private final UpdateNiveauUC updateNiveauUC;
    private final DeleteNiveauUC deleteNiveauUC;
    private final FindNiveauByIdUC findNiveauByIdUC;
    private final FindNiveauxByFiliereIdUC findNiveauxByFiliereIdUC;
    private final SearchNiveauxUC searchNiveauxUC;

    public NiveauResponseDTO createNiveau(CreateNiveauRequestDTO request) {
        return NiveauResponseDTO.fromDomain(createNiveauUC.execute(request));
    }

    public NiveauResponseDTO updateNiveau(Long id, UpdateNiveauRequestDTO request) {
        return NiveauResponseDTO.fromDomain(updateNiveauUC.execute(request, id));
    }

    public void deleteNiveau(Long id) {
        deleteNiveauUC.execute(id);
    }

    public NiveauResponseDTO getNiveauById(Long id) {
        return NiveauResponseDTO.fromDomain(findNiveauByIdUC.execute(id));
    }

    public List<NiveauResponseDTO> getNiveauxByFiliereId(Long filiereId) {
        return findNiveauxByFiliereIdUC.execute(filiereId).stream()
                .map(NiveauResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    public List<NiveauResponseDTO> searchNiveaux(SearchNiveauRequestDTO request) {
        return searchNiveauxUC.execute(request).stream()
                .map(NiveauResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}
