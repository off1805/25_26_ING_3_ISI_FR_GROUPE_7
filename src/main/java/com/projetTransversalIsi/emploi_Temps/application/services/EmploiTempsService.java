package com.projetTransversalIsi.emploi_Temps.application.services;

import com.projetTransversalIsi.emploi_Temps.application.dto.*;
import com.projetTransversalIsi.emploi_Temps.application.use_cases.*;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmploiTempsService implements DefaultEmploiTempsService {

    private final CreateEmploiTempsUC createEmploiTempsUC;
    private final CreateEmploiTempsWithSeancesUC createEmploiTempsWithSeancesUC;
    private final UpdateEmploiTempsUC updateEmploiTempsUC;
    private final UpdateEmploiTempsWithSeancesUC updateEmploiTempsWithSeancesUC;
    private final DeleteEmploiTempsUC deleteEmploiTempsUC;
    private final FindEmploiTempsByIdUC findEmploiTempsByIdUC;
    private final SearchEmploiTempsUC searchEmploiTempsUC;
    private final AddSeanceToEmploiUC addSeanceToEmploiUC;
    private final RemoveSeanceFromEmploiUC removeSeanceFromEmploiUC;

    @Override
    @Transactional
    public EmploiTempsResponseDTO createEmploiTemps(CreateEmploiTempsRequestDTO request) {
        EmploiTemps emploi = createEmploiTempsUC.execute(request);
        return EmploiTempsResponseDTO.fromDomain(emploi);
    }

    @Override
    @Transactional
    public EmploiTempsResponseDTO createEmploiTempsWithSeances(CreateEmploiTempsWithSeancesDTO request) {
        EmploiTemps emploi = createEmploiTempsWithSeancesUC.execute(request);
        return EmploiTempsResponseDTO.fromDomain(emploi);
    }

    @Override
    @Transactional
    public EmploiTempsResponseDTO updateEmploiTemps(UpdateEmploiTempsRequestDTO request) {
        EmploiTemps emploi = updateEmploiTempsUC.execute(request);
        return EmploiTempsResponseDTO.fromDomain(emploi);
    }

    @Override
    @Transactional
    public EmploiTempsResponseDTO updateEmploiTempsWithSeances(UpdateEmploiTempsWithSeancesDTO request) {
        EmploiTemps emploi = updateEmploiTempsWithSeancesUC.execute(request);
        return EmploiTempsResponseDTO.fromDomain(emploi);
    }

    @Override
    @Transactional
    public void deleteEmploiTemps(Long id) {
        deleteEmploiTempsUC.execute(id);
    }

    @Override
    public EmploiTempsResponseDTO getEmploiTempsById(Long id) {
        EmploiTemps emploi = findEmploiTempsByIdUC.execute(id);
        return EmploiTempsResponseDTO.fromDomain(emploi);
    }

    @Override
    public List<EmploiTempsResponseDTO> searchEmplois(SearchEmploiTempsRequestDTO criteria) {
        return searchEmploiTempsUC.execute(criteria).stream()
                .map(EmploiTempsResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmploiTempsResponseDTO addSeanceToEmploi(AddSeanceToEmploiDTO request) {
        EmploiTemps emploi = addSeanceToEmploiUC.execute(request);
        return EmploiTempsResponseDTO.fromDomain(emploi);
    }

    @Override
    @Transactional
    public EmploiTempsResponseDTO removeSeanceFromEmploi(AddSeanceToEmploiDTO request) {
        EmploiTemps emploi = removeSeanceFromEmploiUC.execute(request);
        return EmploiTempsResponseDTO.fromDomain(emploi);
    }
}
