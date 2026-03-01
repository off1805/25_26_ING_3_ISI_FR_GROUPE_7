package com.projetTransversalIsi.Filiere.application.use_cases;

import com.projetTransversalIsi.Filiere.application.dto.SearchFiliereRequestDTO;
import com.projetTransversalIsi.Filiere.domain.Filiere;
import com.projetTransversalIsi.Filiere.domain.FiliereRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchFiliereUCImpl implements SearchFiliereUC {

    private final FiliereRepository filiereRepo;

    @Override
    public List<Filiere> execute(SearchFiliereRequestDTO criteria) {

        List<Filiere> resultats;


        if (criteria.code() != null && !criteria.code().isEmpty()) {
            return filiereRepo.findByCode(criteria.code())
                    .map(List::of)
                    .orElse(List.of());
        }


        if (criteria.nom() != null && !criteria.nom().isEmpty()) {
            resultats = filiereRepo.searchByNom(criteria.nom());
        } else {

            resultats = filiereRepo.findAll();
        }


        if (!criteria.includeDeleted()) {
            resultats = resultats.stream()
                    .filter(f -> !f.isDeleted())
                    .collect(Collectors.toList());
        }

        return resultats;
    }
}