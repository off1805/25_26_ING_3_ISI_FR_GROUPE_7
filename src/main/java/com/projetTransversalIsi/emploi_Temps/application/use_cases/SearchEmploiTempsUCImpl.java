package com.projetTransversalIsi.emploi_Temps.application.use_cases;

import com.projetTransversalIsi.emploi_Temps.application.dto.SearchEmploiTempsRequestDTO;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTemps;
import com.projetTransversalIsi.emploi_Temps.domain.EmploiTempsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchEmploiTempsUCImpl implements SearchEmploiTempsUC {

    private final EmploiTempsRepository emploiTempsRepo;

    @Override
    public List<EmploiTemps> execute(SearchEmploiTempsRequestDTO criteria) {

        List<EmploiTemps> resultats;


        if (criteria.filiereId() != null) {
            resultats = emploiTempsRepo.findByFiliereId(criteria.filiereId());
        }

        else if (criteria.niveauId() != null) {
            resultats = emploiTempsRepo.findByNiveauId(criteria.niveauId());
        }

        else if (criteria.semaine() != null) {
            resultats = emploiTempsRepo.findBySemaine(criteria.semaine());
        }

        else if (criteria.date() != null) {
            resultats = emploiTempsRepo.findByPeriode(criteria.date());
        }

        else {
            resultats = emploiTempsRepo.findAll();
        }


        if (criteria.includeDeleted() != null && !criteria.includeDeleted()) {
            resultats = resultats.stream()
                    .filter(e -> !e.isDeleted())
                    .collect(Collectors.toList());
        }

        return resultats;
    }
}