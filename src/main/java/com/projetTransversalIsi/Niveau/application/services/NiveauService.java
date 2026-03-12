package com.projetTransversalIsi.Niveau.application.services;

import com.projetTransversalIsi.Niveau.application.dto.*;
import com.projetTransversalIsi.Niveau.application.use_cases.*;
import com.projetTransversalIsi.Niveau.domain.Niveau;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NiveauService implements DefaultNiveauService {

    private final CreateNiveauUC createNiveauUC;
    private final UpdateNiveauUC updateNiveauUC;
    private final DeleteNiveauUC deleteNiveauUC;
    private final FindNiveauByIdUC findNiveauByIdUC;
    private final SearchNiveauxUC searchNiveauxUC;

    @Override
    public NiveauResponseDTO createNiveau(CreateNiveauRequestDTO request) {
        Niveau niveau = createNiveauUC.execute(request);
        return NiveauResponseDTO.fromDomain(niveau);
    }

    @Override
    public NiveauResponseDTO updateNiveau(UpdateNiveauRequestDTO request) {
        Niveau niveau = updateNiveauUC.execute(request);
        return NiveauResponseDTO.fromDomain(niveau);
    }

    @Override
    public void deleteNiveau(Long id) {
        deleteNiveauUC.execute(id);
    }

    @Override
    public NiveauResponseDTO getNiveauById(Long id) {
        Niveau niveau = findNiveauByIdUC.execute(id);
        return NiveauResponseDTO.fromDomain(niveau);
    }

    @Override
    public List<NiveauResponseDTO> searchNiveaux(SearchNiveauRequestDTO request) {
        return searchNiveauxUC.execute(request).stream()
                .map(NiveauResponseDTO::fromDomain)
                .collect(Collectors.toList());
    }
}
