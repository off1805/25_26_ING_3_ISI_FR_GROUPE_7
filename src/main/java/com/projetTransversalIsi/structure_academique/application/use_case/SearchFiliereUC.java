package com.projetTransversalIsi.structure_academique.application.use_case;

import com.projetTransversalIsi.structure_academique.application.dto.SearchFiliereRequestDTO;
import com.projetTransversalIsi.structure_academique.domain.model.Filiere;
import com.projetTransversalIsi.structure_academique.domain.repository.FiliereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchFiliereUC {

    private final FiliereRepository filiereRepo;

    public List<Filiere> execute(SearchFiliereRequestDTO criteria) {
        return filiereRepo.search(criteria);
    }
}
