package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import com.projetTransversalIsi.seance.domain.exceptions.SeanceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSeanceUCImpl implements DeleteSeanceUC {

    private final SeanceRepository seanceRepo;

    @Override
    @Transactional
    public void execute(Long id) {

        Seance seance = seanceRepo.findById(id)
                .orElseThrow(() -> new SeanceNotFoundException(id));

        if (seance.isDeleted()) {
            throw new IllegalStateException("La séance " + id + " est déjà supprimée");
        }

        seance.delete();
        seanceRepo.save(seance);
    }
}