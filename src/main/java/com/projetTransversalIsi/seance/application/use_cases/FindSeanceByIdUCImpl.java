package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import com.projetTransversalIsi.seance.domain.exceptions.SeanceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindSeanceByIdUCImpl implements FindSeanceByIdUC {

    private final SeanceRepository seanceRepo;

    @Override
    public Seance execute(Long id) {
        return seanceRepo.findById(id)
                .orElseThrow(() -> new SeanceNotFoundException(id));
    }
}