package com.projetTransversalIsi.Niveau.application.services;

import com.projetTransversalIsi.Niveau.application.dto.*;
import java.util.List;

public interface DefaultNiveauService {
    NiveauResponseDTO createNiveau(CreateNiveauRequestDTO request);
    NiveauResponseDTO updateNiveau(UpdateNiveauRequestDTO request);
    void deleteNiveau(Long id);
    NiveauResponseDTO getNiveauById(Long id);
    List<NiveauResponseDTO> searchNiveaux(SearchNiveauRequestDTO request);
}
