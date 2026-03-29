package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.application.dto.SearchFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchFiliereUCImpl implements SearchFiliereUC {

    private final FiliereRepository filiereRepo;

    @Override
    public List<Filiere> execute(SearchFiliereRequestDTO criteria) {

        return filiereRepo.search(criteria);


    }
}