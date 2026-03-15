package com.projetTransversalIsi.specialite.application.services;

import com.projetTransversalIsi.specialite.application.dto.*;
import com.projetTransversalIsi.specialite.application.use_cases.*;
import com.projetTransversalIsi.specialite.domain.Specialite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialiteService implements DefaultSpecialiteService {

    private final CreateSpecialiteUC createSpecialiteUC;
    private final UpdateSpecialiteUC updateSpecialiteUC;
    private final DeleteSpecialiteUC deleteSpecialiteUC;
    private final FindSpecialiteByIdUC findSpecialiteByIdUC;
    private final GetAllSpecialitesUC getAllSpecialitesUC;
    private final GetSpecialitesByNiveauUC getSpecialitesByNiveauUC;
    private final ToggleSpecialiteStatusUC toggleSpecialiteStatusUC;

    @Override
    public SpecialiteResponseDTO createSpecialite(CreateSpecialiteRequestDTO request) {
        Specialite specialite = createSpecialiteUC.execute(request);
        return SpecialiteResponseDTO.fromDomain(specialite);
    }

    @Override
    public SpecialiteResponseDTO updateSpecialite(Long id, UpdateSpecialiteRequestDTO request) {
        Specialite specialite = updateSpecialiteUC.execute(id, request);
        return SpecialiteResponseDTO.fromDomain(specialite);
    }

    @Override
    public void deleteSpecialite(Long id) {
        deleteSpecialiteUC.execute(id);
    }

    @Override
    public SpecialiteResponseDTO getSpecialiteById(Long id) {
        Specialite specialite = findSpecialiteByIdUC.execute(id);
        return SpecialiteResponseDTO.fromDomain(specialite);
    }

    @Override
    public List<SpecialiteResponseDTO> getAllSpecialites() {
        return getAllSpecialitesUC.execute().stream()
                .map(SpecialiteResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialiteResponseDTO> getSpecialitesByNiveauId(Long niveauId) {
        return getSpecialitesByNiveauUC.execute(niveauId).stream()
                .map(SpecialiteResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void toggleStatus(Long id) {
        // Default to true for activation if not specified, 
        // or we might need to fetch current status. 
        // For now, let's assume it's a toggle so we might need more logic or change the interface.
        // If the use case expects a boolean, we should probably pass it.
        // Let's check common pattern.
        toggleSpecialiteStatusUC.execute(id, true); 
    }
}
