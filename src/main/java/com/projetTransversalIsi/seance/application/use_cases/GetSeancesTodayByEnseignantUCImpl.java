package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetSeancesTodayByEnseignantUCImpl implements GetSeancesTodayByEnseignantUC {

    private final SeanceRepository seanceRepo;

    @Override
    public List<Seance> execute(Long enseignantId, Boolean includeDeleted) {
        if (enseignantId == null) {
            return Collections.emptyList();
        }

        LocalDate today = LocalDate.now();

        List<Seance> result = seanceRepo.findByDate(today).stream()
                .filter(s -> Objects.equals(s.getEnseignantId(), enseignantId))
                .collect(Collectors.toList());

        if (includeDeleted != null && !includeDeleted) {
            result = result.stream()
                    .filter(s -> !s.isDeleted())
                    .collect(Collectors.toList());
        }

        return result;
    }
}

