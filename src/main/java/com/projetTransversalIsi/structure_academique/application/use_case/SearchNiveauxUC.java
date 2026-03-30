package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.SearchNiveauRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Niveau;
import com.projetTransversalIsi.structure_academique.domain.repository.NiveauRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchNiveauxUC {

    private final NiveauRepository niveauRepo;

    public List<Niveau> execute(SearchNiveauRequestDTO request) {
        return niveauRepo.findAll(request);
    }
}
