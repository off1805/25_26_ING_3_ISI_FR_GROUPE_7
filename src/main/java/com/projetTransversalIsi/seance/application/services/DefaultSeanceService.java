package com.projetTransversalIsi.seance.application.services;

import com.projetTransversalIsi.seance.application.dto.*;
import java.util.List;

public interface DefaultSeanceService {

    SeanceResponseDTO createSeance(CreateSeanceRequestDTO request);
    SeanceResponseDTO updateSeance(UpdateSeanceRequestDTO request);
    void deleteSeance(Long id);
    SeanceResponseDTO getSeanceById(Long id);
    List<SeanceResponseDTO> searchSeances(SearchSeanceRequestDTO criteria);
}