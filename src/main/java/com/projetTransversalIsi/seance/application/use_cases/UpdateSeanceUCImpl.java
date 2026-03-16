package com.projetTransversalIsi.seance.application.use_cases;

import com.projetTransversalIsi.seance.application.dto.UpdateSeanceRequestDTO;
import com.projetTransversalIsi.seance.domain.Seance;
import com.projetTransversalIsi.seance.domain.SeanceRepository;
import com.projetTransversalIsi.seance.domain.exceptions.SeanceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateSeanceUCImpl implements UpdateSeanceUC {

    private final SeanceRepository seanceRepo;

    @Override
    @Transactional
    public Seance execute(UpdateSeanceRequestDTO command) {

        Seance seance = seanceRepo.findById(command.id())
                .orElseThrow(() -> new SeanceNotFoundException(command.id()));

        seance.update(
                command.heureDebut(),
                command.heureFin()
        );

        return seanceRepo.save(seance);
    }
}
