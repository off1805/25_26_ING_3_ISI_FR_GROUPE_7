package com.projetTransversalIsi.Filiere.application.services;

import com.projetTransversalIsi.Filiere.application.dto.*;
import java.util.List;

public interface DefaultFiliereService {

    FiliereResponseDTO createFiliere(CreateFiliereRequestDTO request);
    FiliereResponseDTO updateFiliere(UpdateFiliereRequestDTO request,Long id);
    void deleteFiliere(Long id);
    FiliereResponseDTO getFiliereById(Long id);
    List<FiliereResponseDTO> searchFilieres(SearchFiliereRequestDTO criteria);
    List<FiliereResponseDTO> getFilieresByCycleId(Long cycleId);
}
