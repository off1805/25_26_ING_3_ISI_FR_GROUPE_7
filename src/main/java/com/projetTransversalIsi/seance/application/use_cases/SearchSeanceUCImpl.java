package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.application.dto.SearchSeanceRequestDTO;
import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchSeanceUCImpl implements SearchSeanceUC {

    private final SeanceRepository seanceRepo;

    @Override
    public List<Seance> execute(SearchSeanceRequestDTO criteria) {

        List<Seance> resultats;


        if (criteria.date() != null) {
            resultats = seanceRepo.findByDate(criteria.date());
        }

        else if (criteria.enseignantId() != null) {
            resultats = seanceRepo.findByEnseignantId(criteria.enseignantId());
        }

        else if (criteria.coursId() != null) {
            resultats = seanceRepo.findByCoursId(criteria.coursId());
        }

        else {
            resultats = seanceRepo.findAll();
        }


        if (criteria.includeDeleted() != null && !criteria.includeDeleted()) {
            resultats = resultats.stream()
                    .filter(s -> !s.isDeleted())
                    .collect(Collectors.toList());
        }

        return resultats;
    }
}