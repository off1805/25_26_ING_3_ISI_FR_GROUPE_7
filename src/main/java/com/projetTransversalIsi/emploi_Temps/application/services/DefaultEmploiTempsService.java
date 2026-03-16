package com.projetTransversalIsi.emploi_Temps.application.services;

import com.projetTransversalIsi.emploi_Temps.application.dto.*;
import java.util.List;

public interface DefaultEmploiTempsService {

    EmploiTempsResponseDTO createEmploiTemps(CreateEmploiTempsRequestDTO request);
    EmploiTempsResponseDTO createEmploiTempsWithSeances(CreateEmploiTempsWithSeancesDTO request);
    EmploiTempsResponseDTO updateEmploiTempsWithSeances(UpdateEmploiTempsWithSeancesDTO request);
    EmploiTempsResponseDTO updateEmploiTemps(UpdateEmploiTempsRequestDTO request);
    void deleteEmploiTemps(Long id);
    EmploiTempsResponseDTO getEmploiTempsById(Long id);
    List<EmploiTempsResponseDTO> searchEmplois(SearchEmploiTempsRequestDTO criteria);

    EmploiTempsResponseDTO addSeanceToEmploi(AddSeanceToEmploiDTO request);
    EmploiTempsResponseDTO removeSeanceFromEmploi(AddSeanceToEmploiDTO request);
}
