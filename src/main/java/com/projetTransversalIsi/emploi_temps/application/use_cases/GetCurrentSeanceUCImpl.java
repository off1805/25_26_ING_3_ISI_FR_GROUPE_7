package com.projetTransversalIsi.emploi_temps.application.use_cases;

import com.projetTransversalIsi.emploi_temps.application.dto.SeanceResponseDTO;
import com.projetTransversalIsi.emploi_temps.domain.repository.SeanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetCurrentSeanceUCImpl implements GetCurrentSeanceUC {

    private final SeanceRepository seanceRepo;

    @Override
    public Optional<SeanceResponseDTO> execute(Long enseignantId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return seanceRepo.findByEnseignantId(enseignantId)
                .stream()
                .filter(s -> !s.isDeleted())
                .filter(s -> today.equals(s.getDateSeance()))
                .filter(s -> !s.getHeureDebut().isAfter(now) && !s.getHeureFin().isBefore(now))
                .findFirst()
                .map(SeanceResponseDTO::fromDomain);
    }
}
