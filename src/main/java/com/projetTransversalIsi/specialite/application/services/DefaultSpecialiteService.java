package com.projetTransversalIsi.specialite.application.services;

import com.projetTransversalIsi.specialite.application.dto.*;
import java.util.List;

public interface DefaultSpecialiteService {
    SpecialiteResponseDTO createSpecialite(CreateSpecialiteRequestDTO request);
    SpecialiteResponseDTO updateSpecialite(Long id, UpdateSpecialiteRequestDTO request);
    void deleteSpecialite(Long id);
    SpecialiteResponseDTO getSpecialiteById(Long id);
    List<SpecialiteResponseDTO> getAllSpecialites();
    List<SpecialiteResponseDTO> getSpecialitesByNiveauId(Long niveauId);
    void toggleStatus(Long id);
}
